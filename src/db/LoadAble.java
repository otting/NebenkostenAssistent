package db;

public interface LoadAble {
    @Override
    public String toString();

    public House getHouse();

    public Flat getFlat();
}