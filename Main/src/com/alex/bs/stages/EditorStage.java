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

import com.alex.bs.models.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

import java.io.*;

public class EditorStage extends BasicStage {
    private Vector2 moveScreen, moveActor, nowScreen, nowActor;
    private float screenScaleX, screenScaleY;
    private boolean enablePhysics;
    private Actor selectedActor;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public EditorStage(float width, float height) {
        super(width, height, true);

        physicsWorld = new World(new Vector2(0, -9.8f), true);

        Skate skate = new Skate();
        addActor(skate);

        Wall wall = new Wall();
        wall.setPosition(new Vector2(0, -50));
        addActor(wall);
        wall = new Wall();
        wall.setPosition(new Vector2(-200, -50));
        wall.setRotation(-10);
        addActor(wall);
        wall = new Wall();
        wall.setPosition(new Vector2(400, -10));
        wall.setRotation(0);
        addActor(wall);

        Player player = new Player();
        player.setPosition(new Vector2(100, 50));
        addActor(player);

        getCamera().position.set(0, 0, 0f);
    }

    @Override
    public void addActor(Actor actor) {
        actor.addListener(new DragListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(button == 0) {
                    selectedActor = event.getTarget();
                }
                return super.touchDown(event, x, y, pointer, button);
            }


        });
        super.addActor(actor);
    }

    @Override
    public void act(float delta) {
        getCamera().update();
        getSpriteBatch().setProjectionMatrix(getCamera().projection);

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(Actor a : getRoot().getChildren()) {
            //shapeRenderer.rotate(0.f, 0.f, 1.f, a.getRotation());
            shapeRenderer.identity();
            //shapeRenderer.rotate(0, 0, 1, 90);
            shapeRenderer.rect(a.getX(), a.getY(), a.getWidth(), a.getHeight());
        }
        shapeRenderer.end();

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
            case Input.Keys.SPACE:
                enablePhysics = !enablePhysics;
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
