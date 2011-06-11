/**
 * The MIT License
 *
 * Copyright (c) 2010-2011 Sonatype, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.hudsonci.rest.api.build;

import org.hudsonci.rest.api.internal.ConverterSupport;

import org.hudsonci.rest.model.build.TestCaseDTO;
import org.hudsonci.rest.model.build.TestCaseStatusDTO;
import org.hudsonci.rest.model.build.TestSuiteDTO;
import org.hudsonci.rest.model.build.TestsDTO;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.AbstractTestResultAction;

/**
 * Converts {@link AbstractTestResultAction} into {@link TestsDTO} objects.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.1.0
 */
public class TestsConverter
    extends ConverterSupport
{
    public TestsDTO convert(final AbstractTestResultAction<?> source) {
        assert source != null;

        if (source instanceof TestResultAction) {
            return convert((TestResultAction)source);
        }

        log.trace("Converting (ATRA): {}", source);

        TestsDTO target = new TestsDTO();

        target.setTotal(source.getTotalCount());
        target.setFailed(source.getFailCount());
        target.setSkipped(source.getSkipCount());
        
        return target;
    }

    public TestsDTO convert(final TestResultAction source) {
        assert source != null;

        log.trace("Converting (TRA): {}", source);

        TestsDTO target = new TestsDTO();

        target.setTotal(source.getTotalCount());
        target.setFailed(source.getFailCount());
        target.setSkipped(source.getSkipCount());

        TestResult result = source.getResult();
        for (SuiteResult suite : result.getSuites()) {
            target.getSuites().add(convert(suite));
        }
        
        return target;
    }

    public TestSuiteDTO convert(final SuiteResult source) {
        assert source != null;

        log.trace("Converting: {}", source);

        TestSuiteDTO target = new TestSuiteDTO();

        target.setName(source.getName());
        target.setTimeStamp(source.getTimestamp());
        target.setDuration(source.getDuration());
        
        for (CaseResult result : source.getCases()) {
            target.getCases().add(convert(result));
        }
        
        return target;
    }

    public TestCaseDTO convert(final CaseResult source) {
        assert source != null;

        log.trace("Converting: {}", source);

        TestCaseDTO target = new TestCaseDTO();

        target.setName(source.getName());
        target.setClassName(source.getClassName());
        target.setDuration(source.getDuration());
        target.setStatus(convert(source.getStatus()));
        target.setAge(source.getAge());
        target.setErrorDetails(source.getErrorDetails());
        target.setErrorStackTrace(source.getErrorStackTrace());

        return target;
    }

    public TestCaseStatusDTO convert(final CaseResult.Status source) {
        assert source != null;

        log.trace("Converting: {}", source);

        return TestCaseStatusDTO.valueOf(source.name().toUpperCase());
    }
}
