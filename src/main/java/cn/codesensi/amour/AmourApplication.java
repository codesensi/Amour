package cn.codesensi.amour;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@MapperScan("cn.codesensi.amour.**.mapper")
@SpringBootApplication
public class AmourApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmourApplication.class, args);
    }

}
