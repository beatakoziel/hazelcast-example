package com.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;

import java.net.UnknownHostException;

public class HazelcastServer {

    public static void main(String[] args) throws UnknownHostException {
        Config config = HazelcastConfig.getConfig();
        Hazelcast.newHazelcastInstance(config);
    }
}