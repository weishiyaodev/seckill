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
	 * ��ȡ��ɱ�б�ҳ
	 * @param model ����ģ��
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		// ��ȡ�б�ҳ
		List<Seckill> list = seckillService.getSickillList();
		model.addAttribute("list", list);
		// list.jsp + model = ModelAndView
		return "list"; // WEB-INF/jsp/"list".jsp
	}

	/**
	 * ��ɱ��Ʒ����ҳ
	 * @param seckillId ��ɱ��Ʒ�� id
	 * @param model ����ģ��
	 * @return
	 */
	@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
		// ��� seckillId �� null���򷵻ص��б�ҳ
		if (seckillId == null)
			return "redirect:/seckill/list";
		// ���� seckillId ��ȡ seckill ��Ʒ
		Seckill seckill = seckillService.getById(seckillId);
		// ������� id �� MySQL ��û�в�ѯ����Ӧ seckill��˵���������� seckillId �����⣬Ҳ���ص���ɱ�б�ҳ
		if (seckill == null)
			return "forward:/seckill/list";
		// �� seckill ��Ϣ���뵽 model ����ҳ��
		model.addAttribute("seckill", seckill);
		return "detail"; // ����ҳ
	}

	// ajax json
	/** 
	 * ��¶��ɱ��ַ
	 * @param seckillId
	 * @return
	 */
	@RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {
			"application/json;charset=utf-8" })
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable Long seckillId) {
		SeckillResult<Exposer> result;
		try {
			// ����ɱ�����Ϣ��װ�� Exposer ������
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			// ��ɱ�����װ
			result = new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}

	/**
	 * ִ����ɱ����
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
			// ��Ҫע�᣺��Ҫ�����ֻ��Ųſ��Խ�����ɱ����
			return new SeckillResult<SeckillExecution>(false, "δע��");
		try {
			// ���ô洢����
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
			return  new SeckillResult<SeckillExecution>(true, execution);
		} catch (RepeatKillException e) {
			// �ظ���ɱ�쳣
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(true, execution);
		} catch (SeckillCloseException e) {
			// ��ɱ�����쳣
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
			return new SeckillResult<SeckillExecution>(true, execution);
		} catch (Exception e) {
			// ϵͳ�ڲ��쳣
			logger.error(e.getMessage(), e);
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true, execution);
		}
	}
	
	/**
	 * ��ȡϵͳ��ǰʱ��
	 * @return
	 */
	@RequestMapping(value ="/time/now", method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time() {
		Date now = new Date();
		return new SeckillResult<Long>(true, now.getTime());
	}
}
