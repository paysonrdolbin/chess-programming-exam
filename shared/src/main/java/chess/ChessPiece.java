package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece.PieceType type = piece.getPieceType();
        if(type == PieceType.KING){
            moves = getKingMoves(board, myPosition);
        } else if (type == PieceType.BISHOP) {
            moves = getBishopMoves(board, myPosition);
        } else if (type == PieceType.ROOK){
            moves = getRookMoves(board, myPosition);
        } else if (type == PieceType.QUEEN){
            moves = getQueenMoves(board, myPosition);
        } else if (type == PieceType.KNIGHT){
            moves = getKnightMoves(board, myPosition);
        } else if (type == PieceType.PAWN){
            moves = getPawnMoves(board, myPosition);
        }

        return moves;
    }
    // returns King's moves
    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        for(int row = (pos.getRow() - 1); row < (pos.getRow() + 2); row++) {
            for (int col = (pos.getColumn() - 1); col <(pos.getColumn() + 2); col++) {
                if(row != pos.getRow() || pos.getColumn() != col){
                    ChessMove move = new ChessMove(pos, new ChessPosition(row,col), null);
                    if(inBounds(move) && !colorCheck(move, board)){
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }

    // returns Bishop's moves
    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        // up right
        moves.addAll(getDiagonals(1,1,pos,board));
        // up left
        moves.addAll(getDiagonals(1,-1,pos,board));
        // down right
        moves.addAll(getDiagonals(-1,1,pos,board));
        // down left
        moves.addAll(getDiagonals(-1,-1,pos,board));
        return moves;
    }

    // returns Rook's moves
    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        // up moves
        moves.addAll(getDiagonals(1, 0, pos, board));
        // down moves
        moves.addAll(getDiagonals(-1,0, pos, board));
        // right moves
        moves.addAll(getDiagonals(0, 1, pos, board));
        // left moves
        moves.addAll(getDiagonals(0, -1, pos, board));
        return moves;
    }

    // returns Queen's moves
    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(getBishopMoves(board, pos));
        moves.addAll(getRookMoves(board, pos));
        return moves;
    }

    // returns Knight's moves
    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] moveSet = new int[][]{
                {2, 1}, {2, -1}, {1, 2}, {1, -2},
                {-2, 1}, {-2, -1}, {-1, 2}, {-1, -2}
        };
        for(int[] set : moveSet){
            ChessPosition posNew = new ChessPosition(pos.getRow() + set[0], pos.getColumn() + set[1]);
            ChessMove move = new ChessMove(pos, posNew, null);
            if(inBounds(move) && !colorCheck(move, board)){
                moves.add(move);
            }
        }
        return moves;
    }

    // return's Pawn's moves
    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        PieceType[] pieces = {PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
        // Black pawn's available moves
        if(board.getPiece(pos).getTeamColor() == ChessGame.TeamColor.BLACK){
            // moves 2 spaces on starting row
            if(pos.getRow() == 7){
                ChessPosition posMove2 = new ChessPosition(pos.getRow()+2, pos.getColumn());
                ChessMove move2 = new ChessMove(pos, posMove2, null);
                if(board.getPiece(posMove2) != null){
                    moves.add(move2);
                }
            }
            // adds a promotion move for each piece type moving on to the final row
            if(pos.getRow() == 2){
                for(PieceType piece: pieces){
                    ChessPosition posMovePromote = new ChessPosition(1, pos.getColumn());
                    ChessMove movePromote = new ChessMove(pos, posMovePromote, piece);
                    if(board.getPiece(posMovePromote) != null){
                        moves.add(movePromote);
                    }
                }
            // moves one space normally if in bounds
            } else{
                ChessMove move1 = new ChessMove(pos, new ChessPosition(pos.getRow()+1,pos.getColumn()), null);
                if(!colorCheck(move1, board) && inBounds(move1)){
                    moves.add(move1);
                }
            }
        }


        // White pawn's available moves
        if(board.getPiece(pos).getTeamColor() == ChessGame.TeamColor.WHITE){

        }
        return moves;
    }

    // helper function that returns a set of moves depending on direction.
    private Collection<ChessMove> getDiagonals(int dirR, int dirC, ChessPosition pos, ChessBoard board){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = pos.getRow() + dirR;
        int col = pos.getColumn() + dirC;
        while(row < 9 && row > 0 && col < 9 && col > 0){
            ChessPosition posNew = new ChessPosition(row,col);
            ChessMove move = new ChessMove(pos, posNew, null);
            if(board.getPiece(posNew) != null){
                if(!colorCheck(move, board)){
                    moves.add(move);
                }
                break;
            } else{
                moves.add(move);
            }
            col += dirC;
            row += dirR;
            }
        return moves;
    }

    boolean inBounds(ChessMove move){
        if(move.getEndPosition().getRow() <= 8 && move.getEndPosition().getRow() > 0 && move.getEndPosition().getColumn() <= 8 && move.getEndPosition().getColumn() > 0){
            return true;
        }
        return false;
    }

    boolean colorCheck(ChessMove move, ChessBoard board){
        if(board.getPiece(move.getEndPosition()) == null){
            return false;
        } else if (board.getPiece(move.getStartPosition()).getTeamColor() == board.getPiece(move.getEndPosition()).getTeamColor()) {
            return true;
        }
        return false;
    }
}
