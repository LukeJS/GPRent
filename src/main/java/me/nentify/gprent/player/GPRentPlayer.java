package me.nentify.gprent.player;

import org.spongepowered.api.entity.living.player.Player;

import java.lang.ref.WeakReference;

public class GPRentPlayer {

    private WeakReference<Player> weakPlayer;

    public GPRentPlayer(Player player) {
        this.weakPlayer = new WeakReference<>(player);
    }
}
