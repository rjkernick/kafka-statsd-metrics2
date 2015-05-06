/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airbnb.kafka;

import kafka.utils.VerifiableProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class KafkaMetricsDogstatsdReporterTest {

    private VerifiableProperties properties;

    @Before
    public void init() {
        properties = createMock(VerifiableProperties.class);
        expect(properties.props()).andReturn(new Properties());
        expect(properties.getInt("kafka.metrics.polling.interval.secs", 10)).andReturn(11);
        expect(properties.getString("external.kafka.statsd.host", "localhost")).andReturn("127.0.0.1");
        expect(properties.getInt("external.kafka.statsd.port", 8125)).andReturn(1234);
        expect(properties.getString("external.kafka.statsd.metrics.prefix", "")).andReturn("foo");
        expect(properties.getString("external.kafka.statsd.metrics.exclude_regex",
                KafkaMetricsDogstatsdReporter.DEFAULT_REGEX_FILTER)).andReturn("foo");
        expect(properties.getBoolean("external.kafka.statsd.support.tag", true)).andReturn(false);
    }

    @Test
    public void mbean_name_should_match() {
        String name = new KafkaMetricsDogstatsdReporter().getMBeanName();
        assertEquals("kafka:type=com.airbnb.kafka.KafkaMetricsDogstatsdReporter", name);
    }

    @Test
    public void init_should_start_reporter_when_enabled() {
        expect(properties.getBoolean("external.kafka.statsd.reporter.enabled", false)).andReturn(true);

        replay(properties);
        KafkaMetricsDogstatsdReporter reporter = new KafkaMetricsDogstatsdReporter();
        assertFalse("reporter should not be running", reporter.isRunning());
        reporter.init(properties);
        assertTrue("reporter should be running once #init has been invoked", reporter.isRunning());

        verify(properties);

    }

    @Test
    public void init_should_not_start_reporter_when_disabled() {
        expect(properties.getBoolean("external.kafka.statsd.reporter.enabled", false)).andReturn(false);

        replay(properties);
        KafkaMetricsDogstatsdReporter reporter = new KafkaMetricsDogstatsdReporter();
        assertFalse("reporter should not be running", reporter.isRunning());
        reporter.init(properties);
        assertFalse("reporter should NOT be running once #init has been invoked", reporter.isRunning());

        verify(properties);
    }
}