package me.nentify.gprent.tasks;

import me.nentify.gprent.GPRent;
import me.nentify.gprent.claims.RentableClaim;

import java.util.Collection;

public class RentCheckerTask implements Runnable {

    @Override
    public void run() {
        Collection<RentableClaim> rentableClaims = GPRent.instance.getRentableClaims().all();

        for (RentableClaim rentableClaim : rentableClaims) {
            if (rentableClaim.isRented()) {
                rentableClaim.check();
            }
        }
    }
}
