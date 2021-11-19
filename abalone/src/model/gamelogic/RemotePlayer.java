package model.gamelogic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.owlike.genson.annotation.JsonProperty;


public class RemotePlayer extends Player {
    private String recievingPipePath;
    private String sendingPipePath;

    RemotePlayer(@JsonProperty("name") String name, @JsonProperty("recievingPipePath") String recievingPipePath, @JsonProperty("sendingPipePath") String sendingPipePath) throws Exception {
        super(name);
        this.recievingPipePath = recievingPipePath;
        this.sendingPipePath = sendingPipePath;
    }

    private void sendMove(String move) throws FileNotFoundException, IOException {
        try (FileOutputStream sendingPipe = new FileOutputStream(this.sendingPipePath)) {
            try (PrintWriter p = new PrintWriter(sendingPipe)) {
                p.println(move);
            }
        }
    }

    private String readMove() throws FileNotFoundException, IOException {
        try (InputStreamReader recievingPipe = new InputStreamReader(new FileInputStream(this.recievingPipePath))) {
            try (BufferedReader br = new BufferedReader(recievingPipe)) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }
                String move = sb.toString();
                return move.trim();

            }
        }
    }

    public void sendLastMove(GameState gameState) {
            String lastMove = gameState.gameHistory.moves.get(gameState.gameHistory.moves.size() - 1);
            PlayableMove move = Move.newMove(gameState.board, lastMove, this);
            try {
                sendMove(move.getStandardNotation());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
            System.out.println("Sent move: " + lastMove);

    }

    @Override
    public PlayableMove determineMove(GameState gameState) {
        if (gameState.gameHistory.moves.size() > 0) {
            sendLastMove(gameState);
        }
        try {
            String moveStr = readMove();
            System.out.println("Recieved move: " + moveStr);
            return Move.newStandardMove(gameState.board, moveStr, this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    @Override
    public void shutdown(GameState gameState) {
        if (gameState.getWinner() != gameState.getTeamByPlayer(this)) {
            sendLastMove(gameState);
        }
    }
}
