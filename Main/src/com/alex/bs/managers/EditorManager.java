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

import com.alex.bs.helper.*;
import com.alex.bs.listener.GameContactListener;
import com.alex.bs.models.*;
import com.alex.bs.stages.EditorStage;
import com.alex.bs.ui.EditorUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.SnapshotArray;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

import java.io.*;
import java.util.*;

public class EditorManager {
    private EditorStage stage;
    private int counter;
    private SimpleActor selectedActor;
    private EditorUI editorUI;
    private SimpleActor.TYPE creatingObject = SimpleActor.TYPE.NONE;
    private ShapeRenderer shapeRenderer;
    private List<Vector2> newMeshVertices = new ArrayList<Vector2>();
    public String onBeginContactStr, onEndContactStr, onCheckStr;

    public EditorManager(EditorStage stage, ShapeRenderer shapeRenderer) {
        this.stage = stage;
        this.shapeRenderer = shapeRenderer;
    }

    public void setEditorUI(EditorUI editorUI) {
        this.editorUI = editorUI;
    }

    public Wall addWall() {
        Wall wall = new Wall();
        moveToCenter(wall);
        wall.setName("wall_" + counter++);
        selectedActor = wall;
        editorUI.setSelectedActor(selectedActor);
        stage.setSelectedActor(selectedActor);
        stage.addActor(wall);

        return wall;
    }

    public void moveToCenter(SimpleActor actor) {
        Vector2 pos = stage.screenToStageCoordinates(
                new Vector2(Gdx.graphics.getWidth() / 2 + actor.getWidth() / 2,
                        Gdx.graphics.getHeight() / 2 - actor.getHeight() / 2));
        actor.setPosition(pos.x, pos.y);
    }

    public void setSelectedActor(SimpleActor selectedActor) {
        this.selectedActor = selectedActor;
        editorUI.setSelectedActor(selectedActor);
    }

    public void save(String text) {
        FileHandle file = Gdx.files.local("data/levels/editor/" + text);
        file.writeString(new ExportManager(stage, onCheckStr, onBeginContactStr, onEndContactStr).export(), false);
    }

    public boolean load(String text) {
        FileHandle file = Gdx.files.local("data/levels/editor/" + text);
        if(!file.exists())
            return false;

        InputStream streamInit = Gdx.files.internal("data/levels/init.lua").read();
        InputStream streamLevel = file.read();
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
            try {
                LuaFunction onBeginContactLuaFunc = (LuaFunction) globals.rawget("onBeginContact");
                LuaFunction onEndContactLuaFunc = (LuaFunction) globals.rawget("onEndContact");

                GameContactListener contactListener = new GameContactListener(onBeginContactLuaFunc, onEndContactLuaFunc);
                stage.getPhysicsWorld().setContactListener(contactListener);
            } catch (Exception e) {
                System.err.println(e);
            }

            String accumStr = file.readString();
            accumStr = accumStr.replace("\r", "");
            onBeginContactStr = accumStr.substring(accumStr.indexOf("function onBeginContact(contact)\n") + 33,
                    accumStr.indexOf("end\nfunction", accumStr.indexOf("function onBeginContact(contact)\n")));
            onEndContactStr = accumStr.substring(accumStr.indexOf("function onEndContact(contact)\n") + 31,
                    accumStr.indexOf("end\nfunction", accumStr.indexOf("function onEndContact(contact)\n")));
            onCheckStr = accumStr.substring(accumStr.indexOf("function onCheck()\n") + 19,
                    accumStr.length() - 3);

            clear();
            onCreateLuaFunc.call();
        } catch (IOException e) {
            System.err.println(e);
        }

        return true;
    }

    public void addMesh() {
        creatingObject = SimpleActor.TYPE.MESH;
    }

    public void touchUp(int screenX, int screenY, int button) {
        if(creatingObject == SimpleActor.TYPE.MESH) {
            if(button == 0) {
                Vector2 pos = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
                newMeshVertices.add(pos);
            } else if(button == 2) {
                Mesh mesh = new Mesh(newMeshVertices);
                stage.addActor(mesh);
                creatingObject = SimpleActor.TYPE.NONE;
                newMeshVertices.clear();
                mesh.setName("mesh_" + counter++);
                selectedActor = mesh;
                editorUI.setSelectedActor(selectedActor);
                stage.setSelectedActor(selectedActor);
            } else {
                if(newMeshVertices.size() == 0)
                    creatingObject = SimpleActor.TYPE.NONE;
                else
                    newMeshVertices.remove(newMeshVertices.size() - 1);
            }
        }
    }

    public boolean hasCreatingObject() {
        return creatingObject != SimpleActor.TYPE.NONE;
    }

    public void draw() {
        shapeRenderer.identity();

        if(SeparatorHelper.defaultSeparatorHelper.validate(newMeshVertices) == 0) {
            shapeRenderer.setColor(Color.GREEN);
            List<List<Vector2>> listOfList = SeparatorHelper.defaultSeparatorHelper.getSeparated(newMeshVertices, 30);

            for(List<Vector2> polygonVertices : listOfList) {
                for (int i = 0; i < polygonVertices.size(); i++) {
                    Vector2 v1 = polygonVertices.get(i);
                    Vector2 v2 = i >= polygonVertices.size() - 1 ? polygonVertices.get(0) : polygonVertices.get(i + 1);
                    shapeRenderer.line(v1.x, v1.y, v2.x, v2.y);
                }
            }
        } else {
            shapeRenderer.setColor(Color.RED);
            for (int i = 0; i < newMeshVertices.size(); i++) {
                Vector2 v1 = newMeshVertices.get(i);
                Vector2 v2 = i >= newMeshVertices.size() - 1 ? newMeshVertices.get(0) : newMeshVertices.get(i + 1);
                shapeRenderer.line(v1.x, v1.y, v2.x, v2.y);
            }
        }
    }

    public Player addPlayer() {
        Player player = new Player();
        moveToCenter(player);
        player.setName("player_" + counter++);
        selectedActor = player;
        editorUI.setSelectedActor(selectedActor);
        stage.setSelectedActor(selectedActor);
        stage.addActor(player);

        return player;
    }

    public Skate addSkate() {
        Skate skate = new Skate();
        moveToCenter(skate);
        skate.setName("skate_" + counter++);
        selectedActor = skate;
        editorUI.setSelectedActor(selectedActor);
        stage.setSelectedActor(selectedActor);
        stage.addActor(skate);

        return skate;
    }

    public Coin addCoin() {
        Coin coin = new Coin();
        moveToCenter(coin);
        coin.setName("coin_" + counter++);
        selectedActor = coin;
        editorUI.setSelectedActor(selectedActor);
        stage.setSelectedActor(selectedActor);
        stage.addActor(coin);

        return coin;
    }

    public void removeSelectedActor() {
        stage.removeActor(selectedActor);
        selectedActor = null;
        editorUI.setSelectedActor(null);
        stage.setSelectedActor(null);
    }

    public void clear() {
        SnapshotArray<Actor> children = stage.getRoot().getChildren();
        for(int i = children.size - 1; i >= 0; i--) {
            Actor a = children.get(i);
            if(a instanceof SimpleActor)
                stage.removeActor(a);
        }
    }
}
