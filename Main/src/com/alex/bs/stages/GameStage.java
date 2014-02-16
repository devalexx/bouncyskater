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

import com.alex.bs.models.Player;
import com.alex.bs.models.Skate;
import com.alex.bs.models.Wall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
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
    private Action leftAction, rightAction;
    private boolean wonGame;
    private LuaFunction onCreateLuaFunc, onCheckLuaFunc;

    public GameStage(float width, float height) {
        super(width, height, true);

        physicsWorld = new World(new Vector2(0, -9.8f), true);

        skate = new Skate();
        addActor(skate);

        Wall wall = new Wall();
        wall.setPosition(new Vector2(0, -50));
        addActor(wall);
        wall = new Wall();
        wall.setPosition(new Vector2(-200, -50));
        wall.setRotation(-10);
        addActor(wall);
        wall = new Wall();
        wall.setPosition(new Vector2(400, -40));
        wall.setRotation(0);
        addActor(wall);

        player = new Player();
        player.setPosition(new Vector2(100, 50));
        addActor(player);

        InputStream streamInit = Gdx.files.internal("data/levels/init.lua").read();
        InputStream streamLevel = Gdx.files.internal("data/levels/test.lua").read();
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
                    player.applyForceToCenter(new Vector2(0, 30));
                break;
        }

        return super.keyDown(keyCode);
    }
}
