package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SeckillController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	/**
	 * 获取秒杀列表页
	 * @param model 数据模型
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		// 获取列表页
		List<Seckill> list = seckillService.getSickillList();
		model.addAttribute("list", list);
		// list.jsp + model = ModelAndView
		return "list"; // WEB-INF/jsp/"list".jsp
	}

	/**
	 * 秒杀产品详情页
	 * @param seckillId 秒杀商品的 id
	 * @param model 数据模型
	 * @return
	 */
	@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
		// 如果 seckillId 是 null，则返回到列表页
		if (seckillId == null)
			return "redirect:/seckill/list";
		// 根据 seckillId 获取 seckill 商品
		Seckill seckill = seckillService.getById(seckillId);
		// 如果根据 id 在 MySQL 中没有查询到对应 seckill，说明传过来的 seckillId 有问题，也返回到秒杀列表页
		if (seckill == null)
			return "forward:/seckill/list";
		// 把 seckill 信息存入到 model 传回页面
		model.addAttribute("seckill", seckill);
		return "detail"; // 详情页
	}

	// ajax json
	/** 
	 * 暴露秒杀地址
	 * @param seckillId
	 * @return
	 */
	@RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {
			"application/json;charset=utf-8" })
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable Long seckillId) {
		SeckillResult<Exposer> result;
		try {
			// 把秒杀相关信息封装到 Exposer 对象中
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			// 秒杀结果封装
			result = new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}

	/**
	 * 执行秒杀操作
	 * @param seckillId
	 * @param md5
	 * @param phone
	 * @return
	 */
	@RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {
			"application/json;charset=utf-8" })
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
			@PathVariable("md5") String md5, @CookieValue(value = "killPhone", required = false) Long phone) {
		// springMVC valid
		if (phone == null)
			// 需要注册：需要输入手机号才可以进行秒杀操作
			return new SeckillResult<SeckillExecution>(false, "未注册");
		try {
			// 调用存储过程
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
			return  new SeckillResult<SeckillExecution>(true, execution);
		} catch (RepeatKillException e) {
			// 重复秒杀异常
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(true, execution);
		} catch (SeckillCloseException e) {
			// 秒杀结束异常
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
			return new SeckillResult<SeckillExecution>(true, execution);
		} catch (Exception e) {
			// 系统内部异常
			logger.error(e.getMessage(), e);
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true, execution);
		}
	}
	
	/**
	 * 获取系统当前时间
	 * @return
	 */
	@RequestMapping(value ="/time/now", method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time() {
		Date now = new Date();
		return new SeckillResult<Long>(true, now.getTime());
	}
}
