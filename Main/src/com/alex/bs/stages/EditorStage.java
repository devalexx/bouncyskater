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
import com.alex.bs.ui.EditorUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

public class EditorStage extends BasicStage {
    private EditorUI editorUI;
    private Vector2 moveScreen, moveActor, nowScreen, nowActor;
    private float screenScaleX, screenScaleY;
    private boolean enablePhysics;
    private Actor selectedActor;
    private Player player;
    private Skate skate;

    public EditorStage(float width, float height) {
        super(width, height, true);
        setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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

        getCamera().position.set(0, 0, 0f);

        editorUI = new EditorUI();
        editorUI.setFillParent(true);
        editorUI.debug();
        addActor(editorUI);
    }

    @Override
    public void addActor(Actor actor) {
        if(!(actor instanceof Layout)) {
            actor.addListener(new DragListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if(button == 0) {
                        selectedActor = event.getTarget();
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
        }
        super.addActor(actor);
    }

    @Override
    public void draw() {
        editorUI.setVisible(false);
        super.draw();
        editorUI.setVisible(true);

        Vector3 tmpScreenPos = getCamera().position.cpy();
        Vector2 tmpScreenSize = new Vector2(getCamera().viewportWidth, getCamera().viewportHeight);

        getCamera().position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        getCamera().viewportWidth = Gdx.graphics.getWidth();
        getCamera().viewportHeight = Gdx.graphics.getHeight();
        getCamera().update();
        getSpriteBatch().setProjectionMatrix(getCamera().combined);
        editorUI.setPosition(0, 0);
        editorUI.setScale(1, 1);

        if(debug)
            Table.drawDebug(this);
        getSpriteBatch().begin();
        editorUI.draw(getSpriteBatch(), 1);
        getSpriteBatch().end();

        editorUI.setPosition(tmpScreenPos.x - getCamera().viewportWidth / 2 * tmpScreenSize.x / Gdx.graphics.getWidth(),
                tmpScreenPos.y - getCamera().viewportHeight / 2 * tmpScreenSize.x / Gdx.graphics.getWidth());
        editorUI.setScale(tmpScreenSize.x / Gdx.graphics.getWidth(),
                tmpScreenSize.y / Gdx.graphics.getHeight());
        getCamera().position.set(tmpScreenPos);
        getCamera().viewportWidth = tmpScreenSize.x;
        getCamera().viewportHeight = tmpScreenSize.y;
        getCamera().update();
        getSpriteBatch().setProjectionMatrix(getCamera().combined);
    }

    @Override
    public void act(float delta) {
        if(enablePhysics) {
            physicsWorld.step(1 / 60f, 8, 3);
        }

        screenScaleX = getCamera().viewportWidth / Gdx.graphics.getWidth();
        screenScaleY = getCamera().viewportHeight / Gdx.graphics.getHeight();

        super.act(delta);
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch(keyCode) {
            case Input.Keys.P:
                enablePhysics = !enablePhysics;
                break;
            case Input.Keys.SPACE:
                if(player!=null)
                    if(player.isSkateAttached())
                        player.detachSkate();
                    else
                        player.attachSkate(skate);
                break;
            case Input.Keys.UP:
                if(player != null && player.standUp() && player.isPlayerGrounded())
                    player.applyForceToCenter(new Vector2(0, 30));
                break;
        }

        return super.keyDown(keyCode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean result = super.touchDown(screenX, screenY, pointer, button);

        if(button == 1) {
            moveScreen = new Vector2(-screenX * screenScaleX, screenY * screenScaleY);
            nowScreen = new Vector2(getCamera().position.x, getCamera().position.y);
        } else if(button == 0 && selectedActor != null) {
            moveActor = new Vector2(screenX * screenScaleX, -screenY * screenScaleY);
            nowActor = new Vector2(selectedActor.getX(), selectedActor.getY());
        }

        return result;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(moveScreen != null) {
            Vector2 pos = nowScreen.cpy().add(
                    new Vector2(-screenX * screenScaleX, screenY * screenScaleY).sub(moveScreen));
            getCamera().position.set(pos.x, pos.y, 0);
        }

        if(moveActor != null) {
            Vector2 pos = nowActor.cpy().add(
                    new Vector2(screenX * screenScaleX, -screenY * screenScaleY).sub(moveActor));
            selectedActor.setPosition(pos.x, pos.y);
        }

        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(button == 1 && moveScreen != null) {
            moveScreen = null;
            nowScreen = null;
        } else if(button == 0 && moveActor != null) {
            moveActor = null;
            nowActor = null;
            selectedActor = null;
        }

        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean scrolled(int amount) {
        getCamera().viewportWidth = getCamera().viewportWidth * (amount < 0 ? 0.5f : 2);
        getCamera().viewportHeight = getCamera().viewportHeight * (amount < 0 ? 0.5f : 2);

        return super.scrolled(amount);
    }
}
