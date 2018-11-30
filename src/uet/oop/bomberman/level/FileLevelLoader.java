package uet.oop.bomberman.level;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Balloon;
import uet.oop.bomberman.entities.character.enemy.Oneal;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.item.BombItem;
import uet.oop.bomberman.entities.tile.item.FlameItem;
import uet.oop.bomberman.entities.tile.item.SpeedItem;
import uet.oop.bomberman.exceptions.LoadLevelException;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileLevelLoader extends LevelLoader {

	/**
	 * Ma tr?n ch?a th�ng tin b?n �?, m?i ph?n t? l�u gi� tr? k� t? �?c ��?c
	 * t? ma tr?n b?n �? trong t?p c?u h?nh
	 */
	private static char[][] _map;

	public FileLevelLoader(Board board, int level) throws LoadLevelException {
		super(board, level);
	}

	@Override
	public void loadLevel(int level) {
		// DONE: �?c d? li?u t? t?p c?u h?nh /levels/Level{level}.txt
		// DONE: c?p nh?t c�c gi� tr? �?c ��?c v�o _width, _height, _level, _map
		String fileName = "res\\levels\\level" + level + ".txt";

		try (Scanner scanner = new Scanner(new File(fileName))) {
			String firstLine = scanner.nextLine();
			System.out.println(firstLine);
			String[] num;
			num = firstLine.split("\\s+");

			this._level = Integer.parseInt(num[0]);
			this._height = Integer.parseInt(num[1]);
			this._width = Integer.parseInt(num[2]);

			System.out.println(_level + " " + _height + " " + _width);

			_map = new char[_height][_width];
			for (int i = 0; i < _height; i++) {
				String line = scanner.nextLine();
				for (int j = 0; j < _width; j++) {
					_map[i][j] = line.charAt(j);
					System.out.print(_map[i][j]);
				}
				System.out.println("");
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void createEntities() {
		// TODO: t?o c�c Entity c?a m�n ch�i
		// TODO: sau khi t?o xong, g?i _board.addEntity() �? th�m Entity v�o game
		// TODO: ph?n code m?u ? d�?i �? h�?ng d?n c�ch th�m c�c lo?i Entity v�o game
		// TODO: h?y x�a n� khi ho�n th�nh ch?c n�ng load m�n ch�i t? t?p c?u h?nh

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				int pos = x + y * getWidth();

				//Th�m background
				Sprite sprite = Sprite.grass;
				switch (_map[y][x]) {
					case '#':
						//Th�m t�?ng
						sprite = Sprite.wall;
						_board.addEntity(pos, new Wall(x, y, sprite));
						break;
					case '*':
						//Th�m g?ch
						_board.addEntity(x + y * _width,
								new LayeredEntity(x, y,
										new Grass(x, y, Sprite.grass),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;
					case 'x':
						//Th�m c?a
						sprite = Sprite.portal;
						_board.addEntity(pos, new LayeredEntity(x, y,
										new Grass(x, y, Sprite.grass),
										new Portal(x, y, Sprite.portal, _board),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;
					case 'p':
						//Th�m Bomber
						int xBomber = x, yBomber = y;
						_board.addCharacter(new Bomber(Coordinates.tileToPixel(xBomber), Coordinates.tileToPixel(yBomber) + Game.TILES_SIZE, _board));
						Screen.setOffset(0, 0);
						_board.addEntity(xBomber + yBomber * _width, new Grass(xBomber, yBomber, Sprite.grass));
						break;
					case '1':
						// th�m enemy Ballon
						_board.addCharacter(new Balloon(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						_board.addEntity(x + y * _width, new Grass(x, y, Sprite.grass));
						break;
					case 'f':
						//Th�m item L?a
						_board.addEntity(pos,
								new LayeredEntity(x, y,
										new Grass(x, y, Sprite.grass),
										new FlameItem(x, y, Sprite.powerup_flames),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;
					case 'b':
						//Th�m item Bom
						_board.addEntity(pos,
								new LayeredEntity(x, y,
										new Grass(x, y, Sprite.grass),
										new BombItem(x, y, Sprite.powerup_bombs),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;
					case 's':
						//Th�m item Speed
						_board.addEntity(pos,
								new LayeredEntity(x, y,
										new Grass(x, y, Sprite.grass),
										new SpeedItem(x, y, Sprite.powerup_speed),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;

					case '2':
						//Th�m enemy Oneal
						_board.addCharacter(new Oneal(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						_board.addEntity(x + y * _width, new Grass(x, y, Sprite.grass));
						break;

					default:
						_board.addEntity(pos, new Grass(x, y, sprite));
						break;
				}
			}
		}

	}

}