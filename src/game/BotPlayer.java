package game;
import java.io.Serializable;

/**
 * Classe BotPlayer que e o jogador automatico
 */
public class BotPlayer extends Player implements Serializable {

	/**
	 * Metodo construtor
	 * @param id int
	 * @param game Game
	 * @param strength byte
	 */
	public BotPlayer(int id, Game game, byte strength) {
		super(id, game, strength);
	}

	/**
	 * O que indica que o jogador e bot
	 * @return false boolean
	 */
	@Override
	public boolean isHumanPlayer() {
		return false;
	}

}
