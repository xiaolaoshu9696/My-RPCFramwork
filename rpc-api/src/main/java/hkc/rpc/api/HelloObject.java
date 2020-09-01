package hkc.rpc.api;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable{
    private Integer id;
    private String message;

}
