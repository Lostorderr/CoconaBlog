package cn.wujizone.coconablog;

import cn.wujizone.coconablog.mapper.TestMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CoconaBlogApplicationTests {
    @Resource
    private TestMapper testMapper;

    @Test
    void contextLoads() {
        System.out.println("result:"+testMapper.select("admin"));
    }

}
