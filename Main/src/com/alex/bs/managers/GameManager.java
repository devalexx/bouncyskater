/*******************************************************************************
 * Copyright 2013 See AUTHORS file.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE V3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.alex.bs.managers;

import com.alex.bs.listener.GameContactListener;
import com.alex.bs.stages.*;
import com.alex.bs.ui.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

import java.io.*;
import java.util.*;

public class GameManager {
    private GameStage stage;
    private GameUI gameUI;
    private String currentLevelName;
    private LinkedList<String> availableLevels = new LinkedList<String>();

    public GameManager(GameStage stage) {
        this.stage = stage;

        for(FileHandle fileHandle : Gdx.files.internal("data/levels").list())
            availableLevels.add(fileHandle.nameWithoutExtension());
    }

    public GameStage getStage() {
        return stage;
    }

    public void setUI(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public GameUI getGameUI() {
        return gameUI;
    }

    public void loadLvl(String name) {
        if(name == null || !availableLevels.contains(name))
            name = availableLevels.getFirst();

        InputStream streamInit = Gdx.files.internal("data/levels/system/init.lua").read();
        InputStream streamLevel = Gdx.files.internal("data/levels/" + name + ".lua").read();
        Globals globals = JsePlatform.standardGlobals();
        Prototype prototype;
        try {
            prototype = globals.loadPrototype(streamInit, "init_script", "t");
            LuaClosure closure = new LuaClosure(prototype, globals);
            closure.call();

            prototype = globals.loadPrototype(streamLevel, "level_script", "t");
            closure = new LuaClosure(prototype, globals);
            closure.call();

            globals.rawset("stage", CoerceJavaToLua.coerce(stage));
            LuaFunction onCreateLuaFunc = (LuaFunction) globals.rawget("onCreate");
            stage.setOnCreateLuaFunc(onCreateLuaFunc);
            stage.setOnCheckLuaFunc((LuaFunction) globals.rawget("onCheck"));

            try {
                LuaFunction onBeginContactLuaFunc = (LuaFunction) globals.rawget("onBeginContact");
                LuaFunction onEndContactLuaFunc = (LuaFunction) globals.rawget("onEndContact");

                GameContactListener contactListener = new GameContactListener(onBeginContactLuaFunc, onEndContactLuaFunc);
                stage.getPhysicsWorld().setContactListener(contactListener);
            } catch (Exception e) {
                System.err.println(e);
            }

            onCreateLuaFunc.call();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void loadLvl(int diff) {
        String lvl = availableLevels.getFirst();
        try {
            if(currentLevelName != null)
                lvl = String.valueOf(Integer.parseInt(currentLevelName) + diff);
        } catch(Exception e) {}

        loadLvl(lvl);
    }

    public boolean hasLvl(int diff) {
        try {
            if(currentLevelName != null)
                return availableLevels.contains(String.valueOf(Integer.parseInt(currentLevelName) + diff));
        } catch(Exception e) {}

        return false;
    }

    public void win() {
        getGameUI().showGameOverWindow(true);
    }

    public void fail() {
        getGameUI().showGameOverWindow(false);
    }
}
