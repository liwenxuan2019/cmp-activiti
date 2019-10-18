/**
 * Copyright 2019 bejson.com
 */
package io.cmp.modules.gateway.entity;
import lombok.Data;

import java.util.List;
import java.util.Date;
import lombok.Data;
/**
 * Auto-generated: 2019-10-17 17:49:43
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Answers {

        private String answerPat;
        private List<String> menuItems;
        private String answerType;
        private String code;
        private List<String> menuItemsIDs;
        private Date date;

        public void setAnswerPat(String answerPat) {
            this.answerPat = answerPat;
        }
        public String getAnswerPat() {
            return answerPat;
        }

        public void setMenuItems(List<String> menuItems) {
            this.menuItems = menuItems;
        }
        public List<String> getMenuItems() {
            return menuItems;
        }

        public void setAnswerType(String answerType) {
            this.answerType = answerType;
        }
        public String getAnswerType() {
            return answerType;
        }

        public void setCode(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }

        public void setMenuItemsIDs(List<String> menuItemsIDs) {
            this.menuItemsIDs = menuItemsIDs;
        }
        public List<String> getMenuItemsIDs() {
            return menuItemsIDs;
        }

        public void setDate(Date date) {
            this.date = date;
        }
        public Date getDate() {
            return date;
        }

}