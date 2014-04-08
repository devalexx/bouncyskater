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

import com.alex.bs.managers.GameManager;
import com.alex.bs.models.Player;
import com.alex.bs.models.Skate;
import com.alex.bs.ui.GameUI;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.luaj.vm2.LuaFunction;

public class GameStage extends BasicStage {
    private final GameManager gameManager;
    private Skate skate;
    private Player player;
    private boolean endGame, freeze;
    private LuaFunction onCreateLuaFunc, onCheckLuaFunc;
    private GameUI gameUI;
    public static float time;

    public GameStage(float width, float height) {
        super(width, height, true);

        physicsWorld = new World(new Vector2(0, -9.8f), true);

        gameManager = new GameManager(this);
        gameUI = new GameUI(this, gameManager, Gdx.app.getType() != Application.ApplicationType.Desktop);
        gameUI.setFillParent(true);
        addActor(gameUI);
        gameUI.debug();
        gameManager.setUI(gameUI);

        gameManager.loadLvl("2");
    }

    @Override
    public void act(float delta) {
        if(!freeze) {
            physicsWorld.step(1 / 60f, 8, 3);
            time += delta;
        }

        super.act(delta);

        getCamera().position.set(player.getX(), player.getY() + player.getHeight(), 0f);
        getCamera().update();
        getSpriteBatch().setProjectionMatrix(getCamera().projection);

        int check = onCheckLuaFunc.call().toint(1);
        if(check != 0 && !endGame) {
            endGame = true;
            if(check == 1)
                gameManager.win();
            else
                gameManager.fail();

            addAction(Actions.delay(1, Actions.run(new Runnable() {
                @Override
                public void run() {
                    freeze = true;
                }
            })));
        }
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
