package uet.oop.bomberman.entities.character;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.level.Coordinates;

import java.util.Iterator;
import java.util.List;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    protected Keyboard _input;

    /**
     * n?u gi� tr? n�y < 0 th? cho ph�p �?t �?i t�?ng Bomb ti?p theo,
     * c? m?i l?n �?t 1 Bomb m?i, gi� tr? n�y s? ��?c reset v? 0 v� gi?m d?n trong m?i l?n update()
     */
    protected int _timeBetweenPutBombs = 0;
    protected int _timeBetweenEatItem = 0;

    public Bomber(int x, int y, Board board) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
    }

    @Override
    public void update() {
        clearBombs();
        if (!_alive) {
            afterKill();
            return;
        }

        if (_timeBetweenPutBombs < -7500) _timeBetweenPutBombs = 0;
        else _timeBetweenPutBombs--;

        if (_timeBetweenEatItem < -500) _timeBetweenEatItem = 0;
        else _timeBetweenEatItem--;
        animate();

        calculateMove();

        detectPlaceBomb();
    }

    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if (_alive)
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE, this);
    }

    public void calculateXOffset() {
        int xScroll = Screen.calculateXOffset(_board, this);
        Screen.setOffset(xScroll, 0);
    }

    /**
     * Ki?m tra xem c� �?t ��?c bom hay kh�ng? n?u c� th? �?t bom t?i v? tr� hi?n t?i c?a Bomber
     */
    private void detectPlaceBomb() {
        // TODO: ki?m tra xem ph�m �i?u khi?n �?t bom c� ��?c g? v� gi� tr? _timeBetweenPutBombs, Game.getBombRate() c� th?a m?n hay kh�ng
        // TODO:  Game.getBombRate() s? tr? v? s? l�?ng bom c� th? �?t li�n ti?p t?i th?i �i?m hi?n t?i
        // TODO: _timeBetweenPutBombs d�ng �? ng�n ch?n Bomber �?t 2 Bomb c�ng t?i 1 v? tr� trong 1 kho?ng th?i gian qu� ng?n
        // TODO: n?u 3 �i?u ki?n tr�n th?a m?n th? th?c hi?n �?t bom b?ng placeBomb()
        // TODO: sau khi �?t, nh? gi?m s? l�?ng Bomb Rate v� reset _timeBetweenPutBombs v? 0

        if (this._input.space && Game.getBombRate() > 0 && _timeBetweenPutBombs < 0) {
            int xt = Coordinates.pixelToTile(this._x + (double)(this._sprite.getSize() / 2));
            int yt = Coordinates.pixelToTile(this._y + (double)(this._sprite.getSize() / 2) - (double)this._sprite.getSize());
            this.placeBomb(xt, yt);
            Game.addBombRate(-1);
            System.out.println("Bomb RATE: " + Game.getBombRate());
            _timeBetweenPutBombs = 20;
        }
    }

    protected void placeBomb(int x, int y) {
        // TODO: th?c hi?n t?o �?i t�?ng bom, �?t v�o v? tr� (x, y)

        _board.addBomb(new Bomb(x,y,_board));

    }

    private void clearBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while (bs.hasNext()) {
            b = bs.next();
            if (b.isRemoved()) {
                bs.remove();
                Game.addBombRate(1);
            }
        }

    }

    @Override
    public void kill() {
        if (!_alive) return;
        _alive = false;
    }

    @Override
    protected void afterKill() {
        if (_timeAfter > 0) --_timeAfter;
        else {
            _board.endGame();
        }
    }


    @Override
    protected void calculateMove() {
        // TODO: x? l? nh?n t�n hi?u �i?u khi?n h�?ng �i t? _input v� g?i move() �? th?c hi?n di chuy?n
        // TODO: nh? c?p nh?t l?i gi� tr? c? _moving khi thay �?i tr?ng th�i di chuy?n
        int x = 0;
        int y = 0;
        double speed = Game.getBomberSpeed();

        if(_input.down){
            y+= speed;
        }

        if(_input.up){
            y -= speed;
        }

        if(_input.left){
            x-= speed;
        }

        if(_input.right){
            x += speed;
        }
        if(x == 0 && y == 0){
            _moving = false;
        } else{
            _moving = true;
            move(x,y);
        }

    }

    @Override
    public boolean canMove(double x, double y) {
        for (int c = 0; c < 4; c++) {
            double xt = ((_x + x) + c % 2 * 11) / Game.TILES_SIZE;
            double yt = ((_y + y) + c / 2 * 12 - 13) / Game.TILES_SIZE;

            Entity a = _board.getEntity(xt, yt, this);

            if(!a.collide(this))
                return false;
        }

        return true;
    }

    @Override
    public void move(double xa, double ya) {
        // TODO: s? d?ng canMove() �? ki?m tra xem c� th? di chuy?n t?i �i?m �? t�nh to�n hay kh�ng v� th?c hi?n thay �?i t?a �? _x, _y
        // TODO: nh? c?p nh?t gi� tr? _direction sau khi di chuy?n
        if (xa > 0) {
            _direction = 1;
        }

        if (xa < 0) {
            _direction = 3;
        }

        if (ya > 0) {
            _direction = 2;
        }

        if (ya < 0) {
            _direction = 0;
        }
        if(canMove(xa,ya)){

            _y += ya;
            _x += xa;
        }

    }

    @Override
    public boolean collide(Entity e) {
        // TODO: x? l? va ch?m v?i Flame
        // TODO: x? l? va ch?m v?i Enemy

        if(e instanceof Grass){
            return true;
        } else if (e instanceof FlameSegment) {
            this.kill();
            return false;
        } else if (e instanceof Enemy) {
            this.kill();
            return false;
        } else if(e instanceof Bomb){
            return true;
        }
        else if(e instanceof LayeredEntity){
            if(((LayeredEntity) e).getTopEntity() instanceof Grass){
                return true;
            }
            return false;
        }
        else if (e.getSprite() == Sprite.wall) {
            return false;
        }
        return true;

    }

    private void chooseSprite() {
        switch (_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }

    private boolean isCenter(double x, double y){
        return false;
    }
}