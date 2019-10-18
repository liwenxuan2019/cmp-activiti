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
public class Nlp {
    private int errCode;
    private List<Answers> answers;
    private int answersCount;
    private Date startTime;
    private Date endTime;

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }
        public Date getStartTime() {
            return startTime;
        }

        public void setErrCode(int errCode) {
            this.errCode = errCode;
        }
        public int getErrCode() {
            return errCode;
        }

        public void setAnswers(List<Answers> answers) {
            this.answers = answers;
        }
        public List<Answers> getAnswers() {
            return answers;
        }

        public void setAnswersCount(int answersCount) {
            this.answersCount = answersCount;
        }
        public int getAnswersCount() {
            return answersCount;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }
        public Date getEndTime() {
            return endTime;
        }
}
