package com.gree.grih.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private static final Logger logger = LoggerFactory.getLogger(UserVO.class);
    private static final Map<Integer, UserVO> users = new HashMap<>();

    private Integer id;
    private String name;


    static {
        users.put(1, new UserVO(1, "Xiao Ming"));
        users.put(2, new UserVO(2, "Li Hua"));
    }

    public UserVO getById(int id) {
        logger.trace("查询用户 id:{}",id);
        return users.get(id);
    }

    public void add(int id, String name) {
        logger.debug("add new user,id:{},name:{}",id,name);
        users.put(id, new UserVO(id, name));
    }
}
