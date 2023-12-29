package dev.mhpro.packetlib.data;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.math.BigDecimal;

@Getter
@Setter
@SuppressWarnings("unchecked")
public abstract class iVector<T extends Number, X> {
    public abstract T getX();

    public abstract void setX(T number);

    public abstract T getY();

    public abstract void setY(T number);

    public abstract T getZ();

    public abstract void setZ(T number);

    private Number add(Number a, Number b) {
        return new BigDecimal(a.toString()).add(new BigDecimal(b.toString()));
    }

    private Number subtract(Number a, Number b) {
        return new BigDecimal(a.toString()).subtract(new BigDecimal(b.toString()));
    }

    public X add(T x, T y, T z) {
        this.setX((T) add(this.getX(), x));
        this.setY((T) add(this.getY(), y));
        this.setY((T) add(this.getZ(), z));
        return (X) this;
    }

    public X subtract(T x, T y, T z) {
        this.setX((T) subtract(this.getX(), x));
        this.setY((T) subtract(this.getY(), y));
        this.setY((T) subtract(this.getZ(), z));
        return (X) this;
    }

    public X subtract(T num) {
        return subtract(num, num, num);
    }

    public X add(T num) {
        return add(num, num, num);
    }


    @SneakyThrows
    public X offset(T x, T y, T z) {
        return ((iVector<T, X>) this.clone()).add(x, y, z);
    }

    @SneakyThrows
    public X offset(T num) {
        return offset(num, num, num);
    }


}
