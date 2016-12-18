package org.elixir_lang.beam;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.elixir_lang.beam.chunk.Atoms;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static org.elixir_lang.psi.call.name.Module.stripElixirPrefix;

public class TreeStructureProvider implements com.intellij.ide.projectView.TreeStructureProvider {
    /**
     * Allows a plugin to modify the list of children displayed for the specified node in the
     * project view.
     *
     * @param parent   the parent node.
     * @param children the list of child nodes according to the default project structure.
     *                 Elements of the collection are of type {@link ProjectViewNode}.
     * @param settings the current project view settings.
     * @return the modified collection of child nodes, or <code>children</code> if no modifications
     * are required.
     */
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                               @NotNull Collection<AbstractTreeNode> children,
                                               ViewSettings settings) {
        Collection<AbstractTreeNode> modifiedChildren = children;

        if (parent instanceof ProjectViewNode) {
            ProjectViewNode parentProjectViewNode = (ProjectViewNode) parent;
            Object value = parentProjectViewNode.getValue();

            if (value instanceof PsiFile) {
                PsiFile psiFile = (PsiFile) value;

                VirtualFile virtualFile = psiFile.getVirtualFile();

                if (virtualFile != null && Beam.is(virtualFile)) {
                    Project project = parent.getProject();

                    if (project != null) {
                        Beam beam = Beam.from(virtualFile);

                        if (beam != null) {
                            Atoms atoms = beam.atoms();

                            if (atoms != null) {
                                String moduleName = atoms.moduleName();

                                if (moduleName != null) {
                                    final String presentableText;

                                    if (moduleName.startsWith("Elixir.")) {
                                        presentableText = stripElixirPrefix(moduleName);
                                    } else {
                                        /* assume it is an Erlang module name and should be treated as an atom instead
                                           an alias */
                                        presentableText = ":" + moduleName;
                                    }

                                    modifiedChildren.add(
                                            new ProjectViewNode<String>(project, moduleName, settings) {
                                                @Override
                                                public boolean contains(@NotNull VirtualFile file) {
                                                    return false;
                                                }

                                                @NotNull
                                                @Override
                                                public Collection<? extends AbstractTreeNode> getChildren() {
                                                    return Collections.emptyList();
                                                }

                                                @Override
                                                protected void update(PresentationData presentation) {
                                                    presentation.setPresentableText(presentableText);
                                                }
                                            }
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }

        return modifiedChildren;
    }

    /**
     * Returns a user data object of the specified type for the specified selection in the
     * project view.
     *
     * @param selected the list of nodes currently selected in the project view.
     * @param dataName the identifier of the requested data object (for example, as defined in
     *                 {@link PlatformDataKeys})
     * @return the data object, or null if no data object can be returned by this provider.
     * @see DataProvider
     */
    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {
        return null;
    }
}
