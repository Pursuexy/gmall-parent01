package com.itcast.gmall.pms.service.impl;

import com.itcast.gmall.pms.entity.Comment;
import com.itcast.gmall.pms.mapper.CommentMapper;
import com.itcast.gmall.pms.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品评价表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
