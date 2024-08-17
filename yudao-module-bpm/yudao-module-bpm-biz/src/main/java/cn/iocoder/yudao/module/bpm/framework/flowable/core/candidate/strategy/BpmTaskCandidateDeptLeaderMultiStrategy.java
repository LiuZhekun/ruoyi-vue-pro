package cn.iocoder.yudao.module.bpm.framework.flowable.core.candidate.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.util.string.StrUtils;
import cn.iocoder.yudao.module.bpm.framework.flowable.core.candidate.BpmTaskCandidateStrategy;
import cn.iocoder.yudao.module.bpm.framework.flowable.core.enums.BpmTaskCandidateStrategyEnum;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 连续多级部门的负责人 {@link BpmTaskCandidateStrategy} 实现类
 *
 * @author jason
 */
@Component
public class BpmTaskCandidateDeptLeaderMultiStrategy extends BpmTaskCandidateAbstractDeptLeaderStrategy {

    public BpmTaskCandidateDeptLeaderMultiStrategy(DeptApi deptApi) {
        super(deptApi);
    }

    @Override
    public BpmTaskCandidateStrategyEnum getStrategy() {
        return BpmTaskCandidateStrategyEnum.MULTI_DEPT_LEADER_MULTI;
    }

    @Override
    public void validateParam(String param) {
        // TODO @jason：是不是可以 | 分隔 deptId 数组，和 level；这样后续可以加更多的参数。
        // 参数格式: , 分割。前面的部门编号，可以为多个。最后一个为部门层级
        List<Long> params = StrUtils.splitToLong(param, ",");
        List<List<Long>> splitList = CollUtil.split(params, params.size() - 1);
        Assert.isTrue(splitList.size() == 2, "参数格式不匹配");
        deptApi.validateDeptList(splitList.get(0));
        Assert.isTrue(splitList.get(1).get(0) > 0, "部门层级必须大于 0");
    }

    @Override
    public Set<Long> calculateUsers(DelegateExecution execution, String param) {
        // TODO @jason：是不是可以 | 分隔 deptId 数组，和 level；这样后续可以加更多的参数。
        // 参数格式: ,分割。前面的部门Id. 可以为多个。 最后一个为部门层级
        List<Long> params = StrUtils.splitToLong(param, ",");
        List<List<Long>> splitList = CollUtil.split(params, params.size() - 1);
        Long level = splitList.get(1).get(0);
        return getMultiLevelDeptLeaderIds(splitList.get(0), level.intValue());
    }

}