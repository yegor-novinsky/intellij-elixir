package org.elixir_lang.mix.importWizard;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.mix.util.ElixirScriptFileUtil;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyuyou on 15/7/2.
 */
public class ImportedOtpApp {
  private static final Pattern pattern = Pattern.compile("(.*)\\.Mixfile");
  private final String myName;
  private final VirtualFile myRoot;
  private final Set<String> myDeps = ContainerUtil.newHashSet();
  private VirtualFile myIdeaModuleFile;
  private Module myModule;

  public ImportedOtpApp(@NotNull VirtualFile root, @NotNull final VirtualFile appMixFile){
    ElixirFile elixirFile = ElixirScriptFileUtil.createPsi(appMixFile);
    Call[] calls = PsiTreeUtil.getChildrenOfType(elixirFile, Call.class);
    String appName = "";

    if (calls != null) {
      for (Call call : calls) {
        if (org.elixir_lang.structure_view.element.modular.Module.is(call)) {
          String name = call.getName();

          if (name != null) {
            Matcher matcher = pattern.matcher(name);

            if (matcher.matches()) {
              appName = matcher.group(1).toLowerCase();
            }
          }
        }
      }
    }

    myName = appName;
    myRoot = root;

    ApplicationManager.getApplication().runReadAction(new Runnable() {
      @Override
      public void run() {
        addInfoFromAppMixFile(appMixFile);
      }
    });
  }

  @NotNull
  public String getName(){
    return myName;
  }

  @NotNull
  public VirtualFile getRoot(){
    return myRoot;
  }

  @NotNull
  public Set<String> getDeps(){
    return myDeps;
  }

  public void setIdeaModuleFile(@Nullable VirtualFile ideaModuleFile){
    myIdeaModuleFile = ideaModuleFile;
  }

  @Nullable
  public VirtualFile getIdeaModuleFile(){
    return myIdeaModuleFile;
  }

  public Module getModule(){
    return myModule;
  }

  public void setModule(Module module){
    myModule = module;
  }

  @Override
  public String toString() {
    return myName + " (" + myRoot + ")" ;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    ImportedOtpApp that = (ImportedOtpApp) obj;

    return myName.equals(that.myName) && myRoot.equals(that.myRoot);
  }

  @Override
  public int hashCode() {
    int result = myName.hashCode();
    result = 31 * result + myRoot.hashCode();
    return result;
  }

  private void addInfoFromAppMixFile(@NotNull VirtualFile appMixFile){
    // todo: get deps
  }

}
