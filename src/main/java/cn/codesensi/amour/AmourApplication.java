package cn.codesensi.amour;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("cn.codesensi.amour.**.mapper")
@SpringBootApplication
public class AmourApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmourApplication.class, args);
    }

}
