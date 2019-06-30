package gg.ragemc.spigot.knockback;

public interface KnockbackProfile {

    public String getName();

    public boolean isFriction();

    public void setFriction(boolean friction);

    public double getHorizontal();

    public void setHorizontal(double horizontal);

    public double getVertical();

    public void setVertical(double vertical);

}
