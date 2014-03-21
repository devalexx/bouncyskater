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
package com.alex.bs.stages;

import com.alex.bs.listener.GameContactListener;
import com.alex.bs.models.Player;
import com.alex.bs.models.Skate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.IOException;
import java.io.InputStream;

public class GameStage extends BasicStage {
    private Skate skate;
    private Player player;
    private boolean wonGame;
    private LuaFunction onCreateLuaFunc, onCheckLuaFunc;

    public GameStage(float width, float height) {
        super(width, height, true);

        physicsWorld = new World(new Vector2(0, -9.8f), true);

        InputStream streamInit = Gdx.files.internal("data/levels/init.lua").read();
        //InputStream streamLevel = Gdx.files.internal("data/levels/editor/temp.lua").read();
        InputStream streamLevel = Gdx.files.internal("data/levels/2.lua").read();
        Globals globals = JsePlatform.standardGlobals();
        Prototype prototype;
        try {
            prototype = globals.loadPrototype(streamInit, "init_script", "t");
            LuaClosure closure = new LuaClosure(prototype, globals);
            closure.call();

            prototype = globals.loadPrototype(streamLevel, "level_script", "t");
            closure = new LuaClosure(prototype, globals);
            closure.call();

            globals.rawset("stage", CoerceJavaToLua.coerce(this));
            onCreateLuaFunc = (LuaFunction) globals.rawget("onCreate");
            onCheckLuaFunc = (LuaFunction) globals.rawget("onCheck");

            try {
                LuaFunction onBeginContactLuaFunc = (LuaFunction) globals.rawget("onBeginContact");
                LuaFunction onEndContactLuaFunc = (LuaFunction) globals.rawget("onEndContact");

                GameContactListener contactListener = new GameContactListener(onBeginContactLuaFunc, onEndContactLuaFunc);
                physicsWorld.setContactListener(contactListener);
            } catch (Exception e) {
                System.err.println(e);
            }

            onCreateLuaFunc.call();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    public void act(float delta) {
        getCamera().position.set(player.getX(), player.getY(), 0f);
        getCamera().update();
        getSpriteBatch().setProjectionMatrix(getCamera().projection);

        physicsWorld.step(1 / 60f, 8, 3);

        super.act(delta);

        if(onCheckLuaFunc.call().toboolean(1) && !wonGame)
            wonGame = true;

        if(wonGame == true)
            System.out.println("WON");
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch(keyCode) {
            case Input.Keys.SPACE:
                if(player.isSkateAttached())
                    player.detachSkate();
                else
                    player.attachSkate(skate);
                break;
            case Input.Keys.UP:
                if(player.standUp() && player.isPlayerGrounded())
                    player.applyForceToCenter(new Vector2(0, 35));
                break;
        }

        return super.keyDown(keyCode);
    }

    @Override
    public void addActor(Actor actor) {
        if(actor instanceof Player)
            player = (Player) actor;
        if(actor instanceof Skate)
            skate = (Skate) actor;

        super.addActor(actor);
    }

    @Override
    public void draw() {
        super.draw();
        drawDebug();
    }
}
