import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Random;

public class TicTacToe extends JFrame implements ChangeListener, ActionListener {
    private JSlider slider;
    private JButton oButton, xButton;
    private Board board;
    private int lineThickness=4;
    private Color oColor=Color.BLUE, xColor=Color.RED;
    static final char BLANK=' ', O='O', X='X';
    private char position[]={  // Board position (BLANK, O, or X)
            BLANK, BLANK, BLANK,
            BLANK, BLANK, BLANK,
            BLANK, BLANK, BLANK};
    private int wins=0, losses=0, draws=0;  // game count by user
    int turn, strat;
    // Start the game
    public static void main(String args[]) {
        new TicTacToe();
    }

    // Initialize
    public TicTacToe() {
        super("Tic Tac Toe Unbeatable AI");
        JPanel topPanel=new JPanel();
        topPanel.setLayout(new FlowLayout());
        add(topPanel, BorderLayout.NORTH);
        add(board=new Board(), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);
        turn = 0;
    }

    // Change line thickness
    public void stateChanged(ChangeEvent e) {
        lineThickness = slider.getValue();
        board.repaint();
    }

    // Change color of O or X
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==oButton) {
            Color newColor = JColorChooser.showDialog(this, "Choose a new color for O", oColor);
            if (newColor!=null)
                oColor=newColor;
        }
        else if (e.getSource()==xButton) {
            Color newColor = JColorChooser.showDialog(this, "Choose a new color for X", xColor);
            if (newColor!=null)
                xColor=newColor;
        }
        board.repaint();
    }

    // Board is what actually plays and displays the game
    private class Board extends JPanel implements MouseListener {
        private Random random=new Random();
        private int rows[][]={{0,2},{3,5},{6,8},{0,6},{1,7},{2,8},{0,8},{2,6}};
        // Endpoints of the 8 rows in position[] (across, down, diagonally)

        public Board() {
            addMouseListener(this);
        }

        // Redraw the board
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w=getWidth();
            int h=getHeight();
            Graphics2D g2d = (Graphics2D) g;

            // Draw the grid
            g2d.setPaint(Color.WHITE);
            g2d.fill(new Rectangle2D.Double(0, 0, w, h));
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(lineThickness));
            g2d.draw(new Line2D.Double(0, h/3, w, h/3));
            g2d.draw(new Line2D.Double(0, h*2/3, w, h*2/3));
            g2d.draw(new Line2D.Double(w/3, 0, w/3, h));
            g2d.draw(new Line2D.Double(w*2/3, 0, w*2/3, h));

            // Draw the Os and Xs
            for (int i=0; i<9; ++i) {
                double xpos=(i%3+0.5)*w/3.0;
                double ypos=(i/3+0.5)*h/3.0;
                double xr=w/8.0;
                double yr=h/8.0;
                if (position[i]==O) {
                    g2d.setPaint(oColor);
                    g2d.draw(new Ellipse2D.Double(xpos-xr, ypos-yr, xr*2, yr*2));
                }
                else if (position[i]==X) {
                    g2d.setPaint(xColor);
                    g2d.draw(new Line2D.Double(xpos-xr, ypos-yr, xpos+xr, ypos+yr));
                    g2d.draw(new Line2D.Double(xpos-xr, ypos+yr, xpos+xr, ypos-yr));
                }
            }
        }

        // Draw an O where the mouse is clicked
        public void mouseClicked(MouseEvent e) {
            int xpos=e.getX()*3/getWidth();
            int ypos=e.getY()*3/getHeight();
            int pos=xpos+3*ypos;
            if (pos>=0 && pos<9 && position[pos]==BLANK) {
                position[pos]=O;
                repaint();
                putX(pos);  // computer plays
                repaint();
            }
        }

        // Ignore other mouse events
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        // Computer plays X
        void putX(int pos) {

            // Check if game is over
            if (won(O)) {
                newGame(O);
                turn = 0;
            }
            else if (isDraw()) {
                newGame(BLANK);
                turn = 0;
            }
                // Play X, possibly ending the game
            else {
                nextMove(pos);
                if (won(X)) {
                    newGame(X);
                    turn = 0;
                }
                else if (isDraw()) {
                    newGame(BLANK);
                    turn = 0;
                }

            }
        }

        // Return true if player has won
        boolean won(char player) {
            for (int i=0; i<8; ++i)
                if (testRow(player, rows[i][0], rows[i][1]))
                    return true;
            return false;
        }

        // Has player won in the row from position[a] to position[b]?
        boolean testRow(char player, int a, int b) {
            return position[a]==player && position[b]==player
                    && position[(a+b)/2]==player;
        }

        // Play X in the best spot
        void nextMove(int pos) {
            int r=findRow(X);  // complete a row of X and win if possible
            if(r<0)// or try to block O from winning
                r = block();
            if (r<0) {  // otherwise move randomly
                //if player starts with center pick a corner
                if (turn == 0 && pos == 4){
                    r = 0;
                }
                //else pick center
                else if (turn == 0 && pos != 4){
                    if(pos == 1 || pos == 3 || pos == 5 || pos ==7)
                    strat = 1;
                    r = 4;
                }
                else if (turn == 1 && strat == 1) {
                    if (pos == 1 || pos == 3 || pos == 5 || pos == 7) {
                        if (position[1] == O && position[7] == O)
                            r = 0;
                        else if (position[3] == O && position[5] == O)
                            r = 0;
                        else if (position[3] == O && position[1] == O)
                            r = 0;
                        else if (position[5] == O && position[1] == O)
                            r = 2;
                        else if (position[5] == O && position[7] == O)
                            r = 8;
                        else if (position[3] == O && position[7] == O)
                            r = 6;
                        else {
                            do
                                r = random.nextInt(9);
                            while (position[r] != BLANK);
                        }
                    } else if (pos == 0 || pos == 2 || pos == 6 || pos == 8) {
                        if (position[3] == O && position[2] == O)
                            r = 0;
                        else if (position[3] == O && position[8] == O)
                            r = 6;
                        else if (position[5] == O && position[0] == O)
                            r = 2;
                        else if (position[5] == O && position[6] == O)
                            r = 8;
                        else if (position[7] == O && position[2] == O)
                            r = 8;
                        else if (position[7] == O && position[0] == O)
                            r = 6;
                        else if (position[1] == O && position[6] == O)
                            r = 8;
                        else if (position[1] == O && position[8] == O)
                            r = 6;
                        else {
                            do
                                r = random.nextInt(9);
                            while (position[r] != BLANK);
                        }
                    }
                }
                else {
                    do
                        r = random.nextInt(9);
                    while (position[r] != BLANK);
                }
            }
            position[r]=X;
            turn++;
        }

        // Return 0-8 for the position of a blank spot in a row if the
        // other 2 spots are occupied by player, or -1 if no spot exists
        int findRow(char player) {
            for (int i=0; i<8; ++i) {
                int result=find1Row(player, rows[i][0], rows[i][1]);
                if (result>=0)
                    return result;
            }
            return -1;
        }

        // If 2 of 3 spots in the row from position[a] to position[b]
        // are occupied by player and the third is blank, then return the
        // index of the blank spot, else return -1.
        int find1Row(char player, int a, int b) {
            int c=(a+b)/2;  // middle spot
            if (position[a]==player && position[b]==player && position[c]==BLANK)
                return c;
            if (position[a]==player && position[c]==player && position[b]==BLANK)
                return b;
            if (position[b]==player && position[c]==player && position[a]==BLANK)
                return a;
            return -1;
        }
        //checks for potential player wins and blocks it
        int block(){
            if(position[2] == O && position[8] == O && position[5] == BLANK)
                return 5;
            if(position[5] == O && position[8] == O && position[2] == BLANK)
                return 2;
            if(position[2] == O && position[5] == O && position[8] == BLANK)
                return 8;
            if(position[0] == O && position[6] == O && position[3] == BLANK)
                return 3;
            if(position[3] == O && position[6] == O && position[0] == BLANK)
                return 0;
            if(position[3] == O && position[0] == O && position[6] == BLANK)
                return 6;
            if(position[1] == O && position[4] == O && position[7] == BLANK)
                return 7;
            if(position[1] == O && position[7] == O && position[4] == BLANK)
                return 4;
            if(position[7] == O && position[4] == O && position[1] == BLANK)
                return 1;
            if(position[0] == O && position[4] == O && position[8] == BLANK)
                return 8;
            if(position[0] == O && position[8] == O && position[4] == BLANK)
                return 4;
            if(position[8] == O && position[4] == O && position[0] == BLANK)
                return 0;
            if(position[2] == O && position[4] == O && position[6] == BLANK)
                return 6;
            if(position[2] == O && position[6] == O && position[4] == BLANK)
                return 4;
            if(position[6] == O && position[4] == O && position[2] == BLANK)
                return 2;
            if(position[0] == O && position[1] == O && position[2] == BLANK)
                return 2;
            if(position[0] == O && position[2] == O && position[1] == BLANK)
                return 1;
            if(position[1] == O && position[2] == O && position[0] == BLANK)
                return 0;
            if(position[3] == O && position[4] == O && position[5] == BLANK)
                return 5;
            if(position[3] == O && position[5] == O && position[4] == BLANK)
                return 4;
            if(position[5] == O && position[4] == O && position[3] == BLANK)
                return 3;
            if(position[6] == O && position[7] == O && position[8] == BLANK)
                return 8;
            if(position[6] == O && position[8] == O && position[7] == BLANK)
                return 7;
            if(position[8] == O && position[7] == O && position[6] == BLANK)
                return 6;
            return -1;
        }

        // Are all 9 spots filled?
        boolean isDraw() {
            for (int i=0; i<9; ++i)
                if (position[i]==BLANK)
                    return false;
            return true;
        }

        // Start a new game
        void newGame(char winner) {
            repaint();
            turn = 0;
            // Announce result of last game.  Ask user to play again.
            String result;
            if (winner==O) {
                ++wins;
                result = "You Win!";
            }
            else if (winner==X) {
                ++losses;
                result = "AI Win!";
            }
            else {
                result = "Tie";
                ++draws;
            }
            if (JOptionPane.showConfirmDialog(null,
                    "You have "+wins+ " wins, "+losses+" losses, "+draws+" draws\n"
                            +"Play again?\nYou can never win btw", result, JOptionPane.YES_NO_OPTION)
                    !=JOptionPane.YES_OPTION) {
                System.exit(0);
            }

            // Clear the board to start a new game
            for (int j=0; j<9; ++j)
                position[j]=BLANK;
        }
    } // end inner class Board
} // end class TicTacToe