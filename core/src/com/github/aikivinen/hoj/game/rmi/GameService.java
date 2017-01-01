package com.github.aikivinen.hoj.game.rmi;


import com.github.aikivinen.hoj.game.Piece;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameService extends Remote {

    boolean movePiece(Move move) throws RemoteException;
    List<Piece> getPieces() throws  RemoteException;
    void setRemoteService(GameService remoteService) throws RemoteException;
}
