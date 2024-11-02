package io.github.vimisky.luta.applier.mysql;

import io.github.vimisky.luta.applier.mysql.entity.LutaApplierChannel;
import io.github.vimisky.luta.applier.mysql.processor.LutaSuperFastHash;
import io.github.vimisky.luta.applier.mysql.service.LutaApplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class LutaApplierMysqlApplicationTests {

    @Autowired
    public LutaApplierService lutaApplierService;




    @Test
    void testFH(){
        String aa = "fc2a3dbf49b1a865f87595b73e7abe20";
        LutaSuperFastHash lsfh = new LutaSuperFastHash(aa,0,0,aa.length());
        System.out.println(lsfh.calculate());
    }

}
