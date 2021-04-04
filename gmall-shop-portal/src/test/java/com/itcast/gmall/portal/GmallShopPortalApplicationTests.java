package com.itcast.gmall.portal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SpringBootTest
class GmallShopPortalApplicationTests {

	@Test
	void contextLoads() {
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		lock.tryLock();
	}

}
