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
package com.alex.bs.ui;

import com.alex.bs.managers.ResourceManager;
import com.alex.bs.stages.GameStage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameUI extends Table {
    private final Skin skin;
    private final Window menuWindow;
    private final GameStage stage;

    public GameUI(GameStage gameStage, boolean needUIButtons) {
        skin = ResourceManager.getInstance().getSkin();
        stage = gameStage;

        TextButton menuTextButton = new TextButton("Main Menu", skin);
        add(menuTextButton).left();
        add(new Label("Hint or score", skin));
        row();
        add().colspan(2).expand();
        row();
        Touchpad touchpad = new Touchpad(20, skin);
        add(touchpad).width(150).height(150).left();
        VerticalGroup gameButtonsVerticalGroup1 = new VerticalGroup();
        VerticalGroup gameButtonsVerticalGroup2 = new VerticalGroup();
        HorizontalGroup gameButtonsHorizontalGroup = new HorizontalGroup();
        add(gameButtonsHorizontalGroup);
        gameButtonsHorizontalGroup.addActor(gameButtonsVerticalGroup1);
        gameButtonsHorizontalGroup.addActor(gameButtonsVerticalGroup2);

        gameButtonsVerticalGroup1.addActor(new TextButton(" A ", skin));
        gameButtonsVerticalGroup1.addActor(new TextButton(" B ", skin));
        gameButtonsVerticalGroup2.addActor(new TextButton(" X ", skin));
        gameButtonsVerticalGroup2.addActor(new TextButton(" Y ", skin));

        if(!needUIButtons) {
            gameButtonsHorizontalGroup.setVisible(false);
            touchpad.setVisible(false);
        }

        menuWindow = createMenuWindow();
        menuWindow.setMovable(false);
        menuWindow.debug();

        menuTextButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addActor(menuWindow);
                menuWindow.setPosition(getWidth() / 2 - menuWindow.getWidth() / 2,
                        getHeight() / 2 - menuWindow.getHeight() / 2);
            }
        });
    }

    public Window createMenuWindow() {
        Window window = new Window("Menu", skin);
        window.add(new Label("Blablabla", skin)).colspan(2);

        window.row();

        window.add(new TextButton("Prev", skin));
        TextButton closeTextButton = new TextButton("Close", skin);
        window.add(closeTextButton);
        window.add(new TextButton("Next", skin));
        window.pack();

        closeTextButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeActor(menuWindow);
            }
        });

        return window;
    }
}
