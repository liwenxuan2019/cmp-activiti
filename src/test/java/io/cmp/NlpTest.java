package io.cmp;

import com.alibaba.fastjson.JSON;
import io.cmp.modules.gateway.entity.AnswerContent;
import io.cmp.modules.gateway.utils.NlpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NlpTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private  NlpUtils nlpUtils;

    @Test
    public void nlpSaaSQA() {
        AnswerContent answerContent=nlpUtils.nlpSaaSQA("78fa31aaf74e4996893ba3fde1ae03a5","你好","YY","盐田区政府","TelecomRobot_YT");
        logger.info(JSON.toJSONString(answerContent));
    }
}
