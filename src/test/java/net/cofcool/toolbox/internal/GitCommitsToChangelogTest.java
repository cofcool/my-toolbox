package net.cofcool.toolbox.internal;

import net.cofcool.toolbox.BaseTest;
import net.cofcool.toolbox.Tool;
import net.cofcool.toolbox.internal.GitCommitsToChangelog.Style;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GitCommitsToChangelogTest extends BaseTest {

    @Override
    protected Tool instance() {
        return new GitCommitsToChangelog();
    }

    @Test
    void runWithTag() throws Exception {
        new GitCommitsToChangelog().run(args.arg("path", Utils.getTestResourcePath("/")).arg("tag", "1.0.0").arg("out", "./target/changelog-runWithTag.md"));
    }

    @Test
    void run() throws Exception {
        new GitCommitsToChangelog().run(args.arg("path", Utils.getTestResourcePath("/")));
    }

    @Test
    void runWithFull() throws Exception {
        new GitCommitsToChangelog().run(args.arg("path", Utils.getTestResourcePath("/")).arg("full", "true").arg("out", "./target/changelog-runWithFull.md"));
    }

    @Test
    void runWithLogPath() throws Exception {
        new GitCommitsToChangelog().run(args.arg("log", Utils.getTestResourcePath("/gitCommitsToChangelogTest.txt")).arg("out", "./target/changelog-runWithLogPath.md"));
    }

    @Test
    void runWithStyle() throws Exception {
        new GitCommitsToChangelog().run(args.arg("log", Utils.getTestResourcePath("/gitCommitsToChangelogStyleTest.txt")).arg("out", "./target/changelog-runWithStyle.md").arg("style", Style.angular.name()));
    }

    @Test
    void runWithNoTag() throws Exception {
        new GitCommitsToChangelog().run(args.arg("path", Utils.getTestResourcePath("/"))
                .arg("out", "./target/changelog-runWithNoTag.md")
                .arg("no-tag", "true")
        );
    }

    @Test
    void style() {
        Assertions.assertTrue(Style.angular.find("fix(git): test").isPresent());
        Assertions.assertFalse(Style.angular.find("test").isPresent());
        Assertions.assertFalse(Style.simple.find("fix test").isPresent());
    }
}