package Sim_Actors;

public class Car {
    private CarType carType;
    private Customer owner;

    public Customer getOwner() {
        return owner;
    }

    public Car(CarType carType) {
        this.carType = carType;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setOwner(Customer customer) {
        this.owner = customer;
    }

    public String getCarName() {
        return "" + carType + "-" + "Zk." + this.owner.getId();
    }
}
