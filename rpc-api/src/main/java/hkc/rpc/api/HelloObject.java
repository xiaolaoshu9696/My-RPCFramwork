package hkc.rpc.api;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class HelloObject implements Serializable{
    private Integer id;
    private String message;

}
