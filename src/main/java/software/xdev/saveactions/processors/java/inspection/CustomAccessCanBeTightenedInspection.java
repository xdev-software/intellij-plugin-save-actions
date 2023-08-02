package software.xdev.saveactions.processors.java.inspection;

//@formatter:off
import com.intellij.codeInsight.daemon.impl.UnusedSymbolUtil;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionsBundle;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.deadCode.UnusedDeclarationInspectionBase;
import com.intellij.codeInspection.inheritance.ImplicitSubclassProvider;
import com.intellij.codeInspection.visibility.VisibilityInspection;
import com.intellij.java.analysis.JavaAnalysisBundle;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.LambdaUtil;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiQualifiedExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiSyntheticClass;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.impl.FindSuperElementsHelper;
import com.intellij.psi.search.searches.FunctionalExpressionSearch;
import com.intellij.psi.util.ClassUtil;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.JavaPsiRecordUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.containers.ComparatorUtil;
import com.intellij.util.containers.ContainerUtil;
import com.siyeh.ig.fixes.ChangeModifierFix;
import com.siyeh.ig.psiutils.MethodUtils;
import com.siyeh.ig.visibility.ClassEscapesItsScopeInspection;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A public version of {@link AccessCanBeTightenedInspection}.
 *
 * @implNote This is a 1 to 1 copy of {@link AccessCanBeTightenedInspection}.
 * <p/>
 * Copy pasting is required because when extending the following error is thrown error:
 * <code>class com.intellij.codeInspection.visibility.CustomAccessCanBeTightenedInspection cannot access its superclass
 * com.intellij.codeInspection.visibility.AccessCanBeTightenedInspection
 * (com.intellij.codeInspection.visibility.CustomAccessCanBeTightenedInspection is in unnamed module of loader
 * com.intellij.ide.plugins.cl.PluginClassLoader ...;
 * com.intellij.codeInspection.visibility.AccessCanBeTightenedInspection is in unnamed module of loader
 * com.intellij.ide.plugins.cl.PluginClassLoader ...)</code>
 *
 * @see com.intellij.codeInspection.visibility.AccessCanBeTightenedInspection
 */
@SuppressWarnings({"java:S6201", "java:S3776", "java:S125", "java:S3398", "java:S2637", "java:S120", "java:S6541", "java:S135"})
public class CustomAccessCanBeTightenedInspection extends AbstractBaseJavaLocalInspectionTool {
    private static final Logger LOG = Logger.getInstance(CustomAccessCanBeTightenedInspection.class);
    private final VisibilityInspection myVisibilityInspection;

    public CustomAccessCanBeTightenedInspection(@NotNull VisibilityInspection visibilityInspection) {
        myVisibilityInspection = visibilityInspection;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    @NotNull
    public String getGroupDisplayName() {
        return InspectionsBundle.message("group.names.visibility.issues");
    }

    @Override
    @NotNull
    public String getShortName() {
        return VisibilityInspection.SHORT_NAME;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new MyVisitor(holder);
    }

    private class MyVisitor extends JavaElementVisitor {
        private final ProblemsHolder myHolder;
        private final UnusedDeclarationInspectionBase myDeadCodeInspection;

        MyVisitor(@NotNull ProblemsHolder holder) {
            myHolder = holder;
            myDeadCodeInspection = UnusedDeclarationInspectionBase.findUnusedDeclarationInspection(holder.getFile());
        }
        private final Object2IntMap<PsiClass> maxSuggestedLevelForChildMembers = new Object2IntOpenHashMap<>();

        @Override
        public void visitClass(@NotNull PsiClass aClass) {
            checkMember(aClass);
        }

        @Override
        public void visitMethod(@NotNull PsiMethod method) {
            checkMember(method);
        }

        @Override
        public void visitField(@NotNull PsiField field) {
            checkMember(field);
        }

        private void checkMember(@NotNull PsiMember member) {
            if (!myVisibilityInspection.SUGGEST_FOR_CONSTANTS && isConstantField(member)) {
                return;
            }

            PsiClass memberClass = member.getContainingClass();
            PsiModifierList memberModifierList = member.getModifierList();
            if (memberModifierList == null) {
                return;
            }
            int currentLevel = PsiUtil.getAccessLevel(memberModifierList);
            int suggestedLevel = suggestLevel(member, memberClass, currentLevel);
            if (memberClass != null) {
                synchronized (maxSuggestedLevelForChildMembers) {
                    int prevMax = maxSuggestedLevelForChildMembers.getInt(memberClass);
                    maxSuggestedLevelForChildMembers.put(memberClass, Math.max(prevMax, suggestedLevel));
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(member.getName() + ": effective level is '" + PsiUtil.getAccessModifier(suggestedLevel) + "'");
            }

            if (suggestedLevel < currentLevel) {
                if (member instanceof PsiClass) {
                    int memberMaxLevel;
                    synchronized (maxSuggestedLevelForChildMembers) {
                        memberMaxLevel = maxSuggestedLevelForChildMembers.getInt(member);
                    }
                    if (memberMaxLevel > suggestedLevel) {
                        // a class can't have visibility less than its members
                        return;
                    }
                }
                PsiElement toHighlight = currentLevel == PsiUtil.ACCESS_LEVEL_PACKAGE_LOCAL ? ((PsiNameIdentifierOwner)member).getNameIdentifier() :
                        ContainerUtil.find(memberModifierList.getChildren(),
                                element -> element instanceof PsiKeyword && element.getText().equals(PsiUtil.getAccessModifier(currentLevel)));
                // can be null in some strange cases of malbuilt PSI, like in EA-95877
                if (toHighlight != null) {
                    String suggestedModifier = PsiUtil.getAccessModifier(suggestedLevel);
                    String message = PsiModifier.PACKAGE_LOCAL.equals(suggestedModifier)
                            ? JavaAnalysisBundle.message("access.can.be.package.private")
                            : JavaAnalysisBundle.message("access.can.be.0", suggestedModifier);
                    myHolder.registerProblem(toHighlight, message, new ChangeModifierFix(suggestedModifier));
                }
            }
        }

        @PsiUtil.AccessLevel
        private int suggestLevel(@NotNull PsiMember member, PsiClass memberClass, @PsiUtil.AccessLevel int currentLevel) {
            if (member.hasModifierProperty(PsiModifier.PRIVATE) || member.hasModifierProperty(PsiModifier.NATIVE)) {
                return currentLevel;
            }
            if (member instanceof PsiMethod && member instanceof SyntheticElement || !member.isPhysical()) {
                return currentLevel;
            }

            if (member instanceof PsiMethod method) {
                if (!method.getHierarchicalMethodSignature().getSuperSignatures().isEmpty()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(member.getName() + " overrides");
                    }
                    return currentLevel; // overrides
                }
                if (MethodUtils.isOverridden(method)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(member.getName() + " overridden");
                    }
                    return currentLevel;
                }
                if (FindSuperElementsHelper.getSiblingInfoInheritedViaSubClass(method) != null ||
                        JavaPsiRecordUtil.getRecordComponentForAccessor(method) != null) {
                    return currentLevel;
                }
            }
            if (member instanceof PsiEnumConstant) {
                return currentLevel;
            }
            if (member instanceof PsiClass && (member instanceof PsiAnonymousClass ||
                    member instanceof PsiTypeParameter ||
                    member instanceof PsiSyntheticClass ||
                    PsiUtil.isLocalClass((PsiClass)member))) {
                return currentLevel;
            }
            if (memberClass != null && (memberClass.isInterface() || PsiUtil.isLocalClass(memberClass) && member instanceof PsiClass)) {
                return currentLevel;
            }

            if (memberClass != null && member instanceof PsiMethod) {
                if (memberClass.isRecord() && ((PsiMethod)member).isConstructor()) {
                    PsiModifierList modifierList = memberClass.getModifierList();
                    assert modifierList != null; // anonymous records don't exist
                    return PsiUtil.getAccessLevel(modifierList);
                }
                // If class will be subclassed by some framework then it could apply some specific requirements for methods visibility
                // so we just skip it here (IDEA-182709, IDEA-160602)
                for (ImplicitSubclassProvider subclassProvider : ImplicitSubclassProvider.EP_NAME.getExtensions()) {
                    if (!subclassProvider.isApplicableTo(memberClass)) {
                        continue;
                    }
                    ImplicitSubclassProvider.SubclassingInfo info = subclassProvider.getSubclassingInfo(memberClass);
                    if (info == null) {
                        continue;
                    }
                    Map<PsiMethod, ImplicitSubclassProvider.OverridingInfo> methodsInfo = info.getMethodsInfo();
                    if (methodsInfo == null || methodsInfo.containsKey(member)) {
                        return currentLevel;
                    }
                }
            }

            PsiFile memberFile = member.getContainingFile();
            Project project = memberFile.getProject();

            int level = AccessibleVisibilityInspection.getMinVisibilityLevel(myVisibilityInspection, member);
            boolean entryPoint = myDeadCodeInspection.isEntryPoint(member) ||
                    member instanceof PsiField && (UnusedSymbolUtil.isImplicitWrite((PsiVariable)member) || UnusedSymbolUtil.isImplicitRead((PsiVariable)member));
            if (entryPoint && level <= 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(member.getName() + " is entry point");
                }
                return currentLevel;
            }

            PsiPackage memberPackage = getPackage(memberFile);
            if (LOG.isDebugEnabled()) {
                LOG.debug(member.getName() + ": checking effective level for " + member);
            }

            int minLevel = Math.max(PsiUtil.ACCESS_LEVEL_PRIVATE, level);
            AtomicInteger maxLevel = new AtomicInteger(minLevel);
            AtomicBoolean foundUsage = new AtomicBoolean();
            boolean proceed = UnusedSymbolUtil.processUsages(project, memberFile, member, new EmptyProgressIndicator(), null, info -> {
                PsiElement element = info.getElement();
                if (element == null) {
                    return true;
                }
                PsiFile psiFile = info.getFile();
                if (psiFile == null) {
                    return true;
                }

                return handleUsage(member, memberClass, maxLevel, memberPackage, element, psiFile, foundUsage);
            });

            if (proceed && member instanceof PsiClass && LambdaUtil.isFunctionalClass((PsiClass)member)) {
                // there can be lambda implementing this interface implicitly
                FunctionalExpressionSearch.search((PsiClass)member).forEach(functionalExpression -> {
                    PsiFile psiFile = functionalExpression.getContainingFile();
                    return handleUsage(member, memberClass, maxLevel, memberPackage, functionalExpression, psiFile, foundUsage);
                });
            }
            if (!foundUsage.get() && !entryPoint) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(member.getName() + " unused; ignore");
                }
                return currentLevel; // do not propose private for unused method
            }

            @PsiUtil.AccessLevel
            int suggestedLevel = maxLevel.get();
            if (suggestedLevel == PsiUtil.ACCESS_LEVEL_PRIVATE && memberClass == null) {
                suggestedLevel = suggestPackageLocal(member);
            }

            String suggestedModifier = PsiUtil.getAccessModifier(suggestedLevel);
            if (LOG.isDebugEnabled()) {
                LOG.debug(member.getName() + ": effective level is '" + suggestedModifier + "'");
            }

            return suggestedLevel;
        }

        private boolean handleUsage(@NotNull PsiMember member,
                                    @Nullable PsiClass memberClass,
                                    @NotNull AtomicInteger maxLevel,
                                    @Nullable PsiPackage memberPackage,
                                    @NotNull PsiElement element,
                                    @NotNull PsiFile psiFile,
                                    @NotNull AtomicBoolean foundUsage) {
            foundUsage.set(true);
            if (!(psiFile instanceof PsiJavaFile)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("     refd from " + psiFile.getName() + "; set to public");
                }
                maxLevel.set(PsiUtil.ACCESS_LEVEL_PUBLIC);
                return false; // referenced from XML, has to be public
            }
            @PsiUtil.AccessLevel
            int level = getEffectiveLevel(element, psiFile, member, memberClass, memberPackage);
            if (LOG.isDebugEnabled()) {
                LOG.debug("    ref in file " + psiFile.getName() + "; level = " + PsiUtil.getAccessModifier(level) + "; (" + element + ")");
            }
            maxLevel.getAndAccumulate(level, Math::max);

            return level != PsiUtil.ACCESS_LEVEL_PUBLIC;
        }

        @PsiUtil.AccessLevel
        private int getEffectiveLevel(@NotNull PsiElement element,
                                      @NotNull PsiFile file,
                                      @NotNull PsiMember member,
                                      PsiClass memberClass,
                                      PsiPackage memberPackage) {
            if (member instanceof PsiClass && element instanceof PsiJavaCodeReferenceElement) {
                PsiMember exportingMember = ClassEscapesItsScopeInspection.getExportingMember((PsiJavaCodeReferenceElement)element);
                if (exportingMember != null) {
                    PsiModifierList modifierList = exportingMember.getModifierList();
                    assert modifierList != null;
                    return PsiUtil.getAccessLevel(modifierList);
                }
            }
            PsiClass innerClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (memberClass != null && PsiTreeUtil.isAncestor(innerClass, memberClass, false) ||
                    innerClass != null && PsiTreeUtil.isAncestor(memberClass, innerClass, false)) {
                // access from the same file can be via private
                // except when used in annotation:
                // @Ann(value = C.VAL) class C { public static final String VAL = "xx"; }
                // or in implements/extends clauses
                if (AccessibleVisibilityInspection.containsReferenceTo(innerClass.getModifierList(), member) ||
                        AccessibleVisibilityInspection.containsReferenceTo(innerClass.getImplementsList(), member) ||
                        AccessibleVisibilityInspection.containsReferenceTo(innerClass.getExtendsList(), member)) {
                    return suggestPackageLocal(member);
                }

                return !member.hasModifierProperty(PsiModifier.ABSTRACT) &&
                        (myVisibilityInspection.SUGGEST_PRIVATE_FOR_INNERS || !isInnerClass(memberClass)) &&
                        (member.hasModifierProperty(PsiModifier.STATIC) || !calledOnInheritor(element, memberClass))
                        ? PsiUtil.ACCESS_LEVEL_PRIVATE : suggestPackageLocal(member);
            }

            PsiExpression qualifier = getQualifier(element);
            PsiElement resolvedQualifier = qualifier instanceof PsiReference ? ((PsiReference)qualifier).resolve() : null;
            if (resolvedQualifier instanceof PsiVariable) {
                resolvedQualifier = PsiUtil.resolveClassInClassTypeOnly(((PsiVariable)resolvedQualifier).getType());
            }
            PsiPackage qualifierPackage = resolvedQualifier == null ? null : getPackage(resolvedQualifier);
            PsiPackage aPackage = getPackage(file);

            if (samePackage(memberPackage, aPackage)
                    && (qualifierPackage == null || samePackage(qualifierPackage, aPackage))
                    // java 9 will dislike split packages
                    && sameModule(member.getContainingFile(), file)) {
                return suggestPackageLocal(member);
            }

            // can't access protected members via "qualifier.protectedMember = 0;"
            if (qualifier != null) {
                return PsiUtil.ACCESS_LEVEL_PUBLIC;
            }

            if (innerClass != null && memberClass != null && innerClass.isInheritor(memberClass, true)) {
                //access from subclass can be via protected, except for constructors
                PsiElement resolved = element instanceof PsiReference ? ((PsiReference)element).resolve() : null;
                boolean isConstructor = resolved instanceof PsiClass && element.getParent() instanceof PsiNewExpression
                        || resolved instanceof PsiMethod && ((PsiMethod)resolved).isConstructor();
                if (!isConstructor) {
                    return PsiUtil.ACCESS_LEVEL_PROTECTED;
                }
            }
            return PsiUtil.ACCESS_LEVEL_PUBLIC;
        }

        private static boolean calledOnInheritor(@NotNull PsiElement element, PsiClass memberClass) {
            PsiExpression qualifier = getQualifier(element);
            if (qualifier == null) {
                PsiClass enclosingInstance = InheritanceUtil.findEnclosingInstanceInScope(memberClass, element, Conditions.alwaysTrue(), true);
                return enclosingInstance != null && enclosingInstance != memberClass;
            }
            PsiClass qClass = PsiUtil.resolveClassInClassTypeOnly(qualifier.getType());
            return qClass != null && qClass.isInheritor(memberClass, true);
        }
    }

    @Nullable
    private static PsiPackage getPackage(@NotNull PsiElement element) {
        PsiFile file = element.getContainingFile();
        PsiDirectory directory = file == null ? null : file.getContainingDirectory();
        return directory == null ? null : JavaDirectoryService.getInstance().getPackage(directory);
    }

    private static boolean samePackage(PsiPackage package1, PsiPackage package2) {
        return package2 == package1 ||
                package2 != null && package1 != null && Comparing.strEqual(package2.getQualifiedName(), package1.getQualifiedName());
    }

    private static boolean sameModule(PsiFile file1, PsiFile file2) {
        if (file1 == file2) {
            return true;
        }
        if (file1 == null || file2 == null) {
            return false;
        }

        VirtualFile virtualFile1 = file1.getVirtualFile();
        VirtualFile virtualFile2 = file2.getVirtualFile();
        if (virtualFile1 == null || virtualFile2 == null) {
            return ComparatorUtil.equalsNullable(virtualFile1, virtualFile2);
        }

        Module module1 = ProjectRootManager.getInstance(file1.getProject()).getFileIndex().getModuleForFile(virtualFile1);
        Module module2 = ProjectRootManager.getInstance(file2.getProject()).getFileIndex().getModuleForFile(virtualFile2);

        return module1 == module2;
    }

    private static PsiExpression getQualifier(@NotNull PsiElement element) {
        PsiExpression qualifier = null;
        if (element instanceof PsiReferenceExpression) {
            qualifier = ((PsiReferenceExpression)element).getQualifierExpression();
        }
        else if (element instanceof PsiMethodCallExpression) {
            qualifier = ((PsiMethodCallExpression)element).getMethodExpression().getQualifierExpression();
        }

        return qualifier instanceof PsiQualifiedExpression ? null : qualifier;
    }

    private static boolean isInnerClass(@NotNull PsiClass memberClass) {
        return memberClass.getContainingClass() != null || memberClass instanceof PsiAnonymousClass;
    }

    private static boolean isConstantField(PsiMember member) {
        return member instanceof PsiField &&
                member.hasModifierProperty(PsiModifier.STATIC) &&
                member.hasModifierProperty(PsiModifier.FINAL) &&
                ((PsiField)member).hasInitializer();
    }

    @PsiUtil.AccessLevel
    private int suggestPackageLocal(@NotNull PsiMember member) {
        boolean suggestPackageLocal = member instanceof PsiClass && ClassUtil.isTopLevelClass((PsiClass)member)
                ? myVisibilityInspection.SUGGEST_PACKAGE_LOCAL_FOR_TOP_CLASSES
                : myVisibilityInspection.SUGGEST_PACKAGE_LOCAL_FOR_MEMBERS;
        return suggestPackageLocal ? PsiUtil.ACCESS_LEVEL_PACKAGE_LOCAL : PsiUtil.ACCESS_LEVEL_PUBLIC;
    }
}
//@formatter:on
