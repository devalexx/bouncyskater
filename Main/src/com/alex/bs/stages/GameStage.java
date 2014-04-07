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
import com.alex.bs.managers.GameManager;
import com.alex.bs.models.Player;
import com.alex.bs.models.Skate;
import com.alex.bs.screens.GameScreen;
import com.alex.bs.ui.GameUI;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.IOException;
import java.io.InputStream;

public class GameStage extends BasicStage {
    private final GameManager gameManager;
    private Skate skate;
    private Player player;
    private boolean wonGame;
    private LuaFunction onCreateLuaFunc, onCheckLuaFunc;
    private GameUI gameUI;

    public GameStage(float width, float height) {
        super(width, height, true);

        physicsWorld = new World(new Vector2(0, -9.8f), true);

        gameManager = new GameManager(this);
        gameUI = new GameUI(this, gameManager, Gdx.app.getType() != Application.ApplicationType.Desktop);
        gameUI.setFillParent(true);
        addActor(gameUI);
        gameUI.debug();
        gameManager.setUI(gameUI);

        gameManager.load("2");
    }

    @Override
    public void act(float delta) {
        getCamera().position.set(player.getX(), player.getY() + player.getHeight(), 0f);
        getCamera().update();
        getSpriteBatch().setProjectionMatrix(getCamera().projection);

        physicsWorld.step(1 / 60f, 8, 3);

        super.act(delta);

        if(onCheckLuaFunc.call().toboolean(1) && !wonGame)
            wonGame = true;

        if(wonGame)
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
        gameUI.setVisible(false);
        super.draw();
        gameUI.setVisible(true);
        drawDebug();

        getCamera().position.set(0, 0, 0);
        getCamera().update();
        getSpriteBatch().setProjectionMatrix(getCamera().combined);
        gameUI.setPosition(-getCamera().viewportWidth / 2, -getCamera().viewportHeight / 2);
        getSpriteBatch().begin();
        gameUI.draw(getSpriteBatch(), 1);
        getSpriteBatch().end();

        gameUI.setPosition((int)getCamera().position.x - getCamera().viewportWidth / 2,
                (int)getCamera().position.y - getCamera().viewportHeight / 2);
        if(debug)
            Table.drawDebug(this);
    }

    public void setOnCreateLuaFunc(LuaFunction onCreateLuaFunc) {
        this.onCreateLuaFunc = onCreateLuaFunc;
    }

    public void setOnCheckLuaFunc(LuaFunction onCheckLuaFunc) {
        this.onCheckLuaFunc = onCheckLuaFunc;
    }
}
