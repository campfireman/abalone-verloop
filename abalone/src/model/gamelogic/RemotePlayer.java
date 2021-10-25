package model.gamelogic;

import com.owlike.genson.annotation.JsonProperty;

import view.UI;

public class RemotePlayer extends Player {
    //

    RemotePlayer(@JsonProperty("name") String name) {
        super(name);
        System.out.println("Spawning remote player!");
    }

    @Override
    public PlayableMove determineMove(GameState gameState) {
        return UI.getInstance().getPlayerMove(this, gameState.getBoard());
    }

}
