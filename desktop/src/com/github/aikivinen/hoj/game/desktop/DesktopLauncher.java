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


    public static void main(String[] arg) throws RemoteException {
        MyGdxGame gameInstance = new MyGdxGame();

        String serverHostname = "localhost";
        if (arg.length > 0 && arg[0].equals("server")) {
            serverHostname = ""
        }
        else if (arg.length > 0) {
            serverHostname = arg[0];
        }

        System.out.println(
            "Starting game. Server mode: " + serverHostname.isEmpty()
        );

        initRmi(isServer, serverHostname, gameInstance);

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 640;
        config.width = 800;
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
