package org.seckill.exception;

/**
 * ��ɱ�ر��쳣
 * @author wei.shiyao
 *
 */
public class SeckillCloseException extends SeckillException {

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeckillCloseException(String message) {
		super(message);
	}
}
