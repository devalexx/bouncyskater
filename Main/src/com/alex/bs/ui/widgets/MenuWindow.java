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
package com.alex.bs.ui.widgets;

import com.alex.bs.managers.GameManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuWindow extends Window {
    private GameManager gameManager;
    TextButton prevTextButton, closeTextButton, nextTextButton;

    public MenuWindow(String title, Skin skin, GameManager manager) {
        super(title, skin);
        gameManager = manager;
        add(new Label("Blablabla", skin)).colspan(2);

        row();

        TextButton prevTextButton;
        prevTextButton = new TextButton("Prev", skin);
        add(prevTextButton);
        closeTextButton = new TextButton("Close", skin);
        add(closeTextButton);
        nextTextButton = new TextButton("Next", skin);
        add(nextTextButton);
        pack();

        final Window self = this;
        closeTextButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        prevTextButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameManager.loadLvl(-1);
                hide();
            }
        });
        nextTextButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameManager.loadLvl(1);
                hide();
            }
        });
        nextTextButton.setDisabled(true);
    }

    public void show() {
        prevTextButton.setDisabled(!gameManager.hasLvl(-1));

        gameManager.getGameUI().addActor(this);
        setPosition(gameManager.getGameUI().getWidth() / 2 - getWidth() / 2,
                gameManager.getGameUI().getHeight() / 2 - getHeight() / 2);
    }

    public void hide() {
        gameManager.getGameUI().removeActor(this);
    }
}
