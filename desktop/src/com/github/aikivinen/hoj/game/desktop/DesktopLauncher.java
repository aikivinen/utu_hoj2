package com.github.aikivinen.hoj.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.aikivinen.hoj.game.MyGdxGame;
import com.github.aikivinen.hoj.game.rmi.GameService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DesktopLauncher {

    public static final int PORT = 4000;
    public static final String GAME_NAME = "foxgame";

    public static final int SCREEN_HEIGHT = 640;
    public static final int SCREEN_WIDTH = 800;

    public static void main(String[] arg) throws RemoteException {

        String serverHostname = "localhost";
        MyGdxGame.Type playerType = MyGdxGame.Type.HOUNDS;
        if (arg.length > 0 && arg[0].equals("server")) {
            serverHostname = "";
            playerType = MyGdxGame.Type.FOX;
        }
        else if (arg.length > 0) {
            serverHostname = arg[0];
        }
        
        MyGdxGame gameInstance = new MyGdxGame(
            SCREEN_HEIGHT,
            SCREEN_WIDTH,
            playerType
        );

        System.out.println(
            "Starting game. Server mode: " + serverHostname.isEmpty()
        );

        initRmi(serverHostname, gameInstance);

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = SCREEN_HEIGHT;
        config.width = SCREEN_WIDTH;
        new LwjglApplication(gameInstance, config);
    }

    private static void initRmi(String serverHostname, MyGdxGame instance) {
        if (serverHostname.isEmpty()) {

            try {
                GameService stub =
                    (GameService) UnicastRemoteObject.exportObject(instance, 0);
                Registry registry = LocateRegistry.createRegistry(PORT);
                registry.rebind(GAME_NAME, stub);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Registry registry =
                                LocateRegistry.getRegistry(serverHostname, PORT);
                GameService stub = (GameService) registry.lookup(GAME_NAME);
                instance.setRemote(stub);

                GameService callback =
                    (GameService) UnicastRemoteObject.exportObject(instance, 0);
                stub.setRemoteService(callback);

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }

        }
    }
}
