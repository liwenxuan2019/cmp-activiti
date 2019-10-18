/**
 * Copyright 2019 bejson.com
 */
package io.cmp.modules.gateway.entity;
import lombok.Data;

import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * Auto-generated: 2019-10-17 17:49:43
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class AnswerContent {
    private int errCode;
    private List<Answers> answers;
    private int answersCount;
    private String startTime;
    private String endTime;
}
