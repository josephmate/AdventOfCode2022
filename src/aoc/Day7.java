package aoc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *--- Day 7: No Space Left On Device ---
 *
 * You can hear birds chirping and raindrops hitting leaves as the expedition proceeds.
 * Occasionally, you can even hear much louder sounds in the distance; how big do the animals get
 * out here, anyway?
 *
 * The device the Elves gave you has problems with more than just its communication system. You
 * try to run a system update:
 *
 * $ system-update --please --pretty-please-with-sugar-on-top
 * Error: No space left on device
 *
 * Perhaps you can delete some files to make space for the update?
 *
 * You browse around the filesystem to assess the situation and save the resulting terminal output
 * (your puzzle input). For example:
 *
 * $ cd /
 * $ ls
 * dir a
 * 14848514 b.txt
 * 8504156 c.dat
 * dir d
 * $ cd a
 * $ ls
 * dir e
 * 29116 f
 * 2557 g
 * 62596 h.lst
 * $ cd e
 * $ ls
 * 584 i
 * $ cd ..
 * $ cd ..
 * $ cd d
 * $ ls
 * 4060174 j
 * 8033020 d.log
 * 5626152 d.ext
 * 7214296 k
 *
 * The filesystem consists of a tree of files (plain data) and directories (which can contain other
 * directories or files). The outermost directory is called /. You can navigate around the
 * filesystem, moving into or out of directories and listing the contents of the directory you're currently in.
 *
 * Within the terminal output, lines that begin with $ are commands you executed, very much like
 * some modern computers:
 *
 *     cd means change directory. This changes which directory is the current directory, but the
 *     specific result depends on the argument:
 *         cd x moves in one level: it looks in the current directory for the directory named x andmakes it the current directory.
 *         cd .. moves out one level: it finds the directory that contains the current directory, then makes that directory the current directory.
 *         cd / switches the current directory to the outermost directory, /.
 *     ls means list. It prints out all of the files and directories immediately contained by the current directory:
 *         123 abc means that the current directory contains a file named abc with size 123.
 *         dir xyz means that the current directory contains a directory named xyz.
 *
 * Given the commands and output in the example above, you can determine that the filesystem looks visually like this:
 *
 * - / (dir)
 *   - a (dir)
 *     - e (dir)
 *       - i (file, size=584)
 *     - f (file, size=29116)
 *     - g (file, size=2557)
 *     - h.lst (file, size=62596)
 *   - b.txt (file, size=14848514)
 *   - c.dat (file, size=8504156)
 *   - d (dir)
 *     - j (file, size=4060174)
 *     - d.log (file, size=8033020)
 *     - d.ext (file, size=5626152)
 *     - k (file, size=7214296)
 *
 * Here, there are four directories: / (the outermost directory), a and d (which are in /),
 * and e (which is in a). These directories also contain files of various sizes.
 *
 * Since the disk is full, your first step should probably be to find directories that are good
 * candidates for deletion. To do this, you need to determine the total size of each directory.
 * The total size of a directory is the sum of the sizes of the files it contains, directly or
 * indirectly. (Directories themselves do not count as having any intrinsic size.)
 *
 * The total sizes of the directories above can be found as follows:
 *
 *     The total size of directory e is 584 because it contains a single file i of size 584 and no
 *     other directories.
 *     The directory a has total size 94853 because it contains files f (size 29116), g (size 2557),
 *     and h.lst (size 62596), plus file i indirectly (a contains e which contains i).
 *     Directory d has total size 24933642.
 *     As the outermost directory, / contains every file. Its total size is 48381165, the sum of
 *     the size of every file.
 *
 * To begin, find all of the directories with a total size of at most 100000,
 * then calculate the sum of their total sizes. In the example above, these directories are a and e;
 * the sum of their total sizes is 95437 (94853 + 584). (As in this example, this process can count files more than once!)
 *
 * Find all of the directories with a total size of at most 100000. What is the sum of the total
 * sizes of those directories?
 *
 * --- Part Two ---
 *
 * Now, you're ready to choose a directory to delete.
 *
 * The total disk space available to the filesystem is 70000000. To run the update, you need unused
 * space of at least 30000000. You need to find a directory you can delete that will free up enough
 * space to run the update.
 *
 * In the example above, the total size of the outermost directory (and thus the total amount of used space) is 48381165;
 * this means that the size of the unused space must currently be 21618835, which isn't quite the 30000000 required by the update.
 * Therefore, the update still requires a directory with total size of at least 8381165 to be deleted before it can run.
 *
 * To achieve this, you have the following options:
 *                                                             (30000000)
 *     Delete directory e, which would increase unused space by 584.
 *     Delete directory a, which would increase unused space by 94853.
 *     Delete directory d, which would increase unused space by 24933642.
 *     Delete directory /, which would increase unused space by 48381165.
 *
 * Directories e and a are both too small; deleting them would not free up enough space. However,
 * directories d and / are both big enough! Between these, choose the smallest: d, increasing
 * unused space by 24933642.
 *
 * Find the smallest directory that, if deleted, would free up enough space on the filesystem
 * to run the update. What is the total size of that directory?
 */
public class Day7 {

    record CD (String name) {

    }

    record LS () {

    }

    record Directory (String name) {

    }

    record FileSize (long fileSize, String name) {

    }

    private static Object parse(String line) {
        String[] columns = line.split(" ");
        if (line.startsWith("$ cd")) {
            return new CD(columns[2]);
        } else if (line.startsWith("$ ls")) {
            return new LS();
        } else if (line.startsWith("dir")){
            return new Directory(columns[1]);
        } else {
            return new FileSize(Long.parseLong(columns[0]), columns[1]);
        }
    }

    private static class MutableDirectory {
        long directSize;
        boolean computedCost;
        long indirectSize;

        Map<String, MutableDirectory> subDirectories = new HashMap<>();
    }

    private static long calculateDirectorySize(MutableDirectory cwd) {
        if (cwd.computedCost) {
            return cwd.indirectSize;
        }

        long indirectSize = cwd.directSize;
        for(MutableDirectory subDir : cwd.subDirectories.values()) {
            indirectSize += calculateDirectorySize(subDir);
        }

        cwd.computedCost = true;
        cwd.indirectSize = indirectSize;
        return indirectSize;
    }

    private static long sumAtMost(MutableDirectory cwd, long limit) {
        long sum = 0;
        if (cwd.indirectSize <= limit) {
            sum += cwd.indirectSize;
        }

        for(MutableDirectory subDir : cwd.subDirectories.values()) {
            sum += sumAtMost(subDir, limit);
        }

        return sum;
    }

    private static MutableDirectory makeDirectories(String input) {
        Deque<MutableDirectory> pathSoFar = new ArrayDeque<>();

        Iterator<Object> commands = input.lines()
            .map(Day7::parse)
            .iterator();

        MutableDirectory root = new MutableDirectory();
        MutableDirectory cwd = root;
        commands.next(); // skip root

        while(commands.hasNext()) {
            Object command = commands.next();
            switch (command) {
                case CD(String name):
                    //System.out.println("cd " + name);
                    if ("..".equals(name)) {
                        cwd = pathSoFar.removeLast();
                    } else {
                        pathSoFar.addLast(cwd);
                        cwd = cwd.subDirectories.get(name);
                    }

                    break;
                case LS():
                    // NO OP
                    //System.out.println("$ ls");
                    break;
                case Directory(String name):
                    //System.out.println("dir " + name);
                    cwd.subDirectories.put(name, new MutableDirectory());
                    break;
                case FileSize(long fileSize, String name):
                    //System.out.println(fileSize + " " + name);
                    cwd.directSize += fileSize;
                    break;
                default:
                    throw new IllegalStateException("unrecognized command: " + command);
            }
        }
        calculateDirectorySize(root);

        return root;
    }

    private static long part1(String input) {
        MutableDirectory root = makeDirectories(input);
        return sumAtMost(root, 100000L);
    }

    private static long findSmallest(MutableDirectory cwd, long spaceToFree) {
        long smallestSoFar = Long.MAX_VALUE;
        if (cwd.indirectSize >= spaceToFree) {
            smallestSoFar = cwd.indirectSize;
        }

        for(MutableDirectory subDir : cwd.subDirectories.values()) {
            long current = findSmallest(subDir, spaceToFree);
            if (current < smallestSoFar) {
                smallestSoFar = current;
            }
        }

        return smallestSoFar;
    }

    /**
     * The total disk space available to the filesystem is 70000000.
     * To run the update, you need unused space of at least 30000000.
     */
    private static long part2(String input) {
        MutableDirectory root = makeDirectories(input);

        //                 space we need  capacity     space we used
        long spaceToFree = 30000000 - (70000000-root.indirectSize);



        return findSmallest(root, spaceToFree);
    }

    public static void main(String[] args) {
        System.out.println(95437);
        System.out.println(part1(SAMPLE));
        System.out.println(part1(INPUT));
        System.out.println("==========");
        System.out.println(24933642);
        System.out.println(part2(SAMPLE));
        System.out.println(part2(INPUT));
    }

    private static final String SAMPLE = """
$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k
""";

    private static final String INPUT = """
$ cd /
$ ls
dir ctd
80649 mwcj.pmh
212527 nbb.ztq
dir pgqmwn
152170 scr.smr
17637 snqcgbs.nhv
dir tmw
dir vtm
$ cd ctd
$ ls
dir bblsqnwl
dir gzcjrs
dir pgqmwn
dir qzgjp
dir shmvmqv
$ cd bblsqnwl
$ ls
dir rng
$ cd rng
$ ls
dir jncpmzcs
dir pttvmghm
293353 qcjbfggw.tjj
dir sgm
$ cd jncpmzcs
$ ls
36126 tstwmrbr.bmr
$ cd ..
$ cd pttvmghm
$ ls
38802 ctttjn.nzr
$ cd ..
$ cd sgm
$ ls
dir djcbdbgr
dir gqrr
$ cd djcbdbgr
$ ls
254050 nbb.ztq
$ cd ..
$ cd gqrr
$ ls
168685 jgsqvf.tql
302004 shmvmqv
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd gzcjrs
$ ls
319972 bwnjnmmh.wwd
25495 fzthlhcp.nst
291270 ghjc.nvf
244664 jrlfb.fvz
$ cd ..
$ cd pgqmwn
$ ls
dir zpvthlgp
$ cd zpvthlgp
$ ls
128092 dqvcqs.zpb
140511 nvqsqtjw
$ cd ..
$ cd ..
$ cd qzgjp
$ ls
23062 jgsqvf.tql
254685 nbb.ztq
dir pgqmwn
320683 shmvmqv
105368 shmvmqv.hmg
60805 snqcgbs
$ cd pgqmwn
$ ls
47287 gffvwmr.pbd
$ cd ..
$ cd ..
$ cd shmvmqv
$ ls
80751 crtwwqb
dir dbfm
dir dhhghph
dir pgqmwn
195177 qnm
dir qpbn
dir rwmsqdw
dir shmvmqv
16133 shmvmqv.vwq
187938 tmw
$ cd dbfm
$ ls
dir gbpnjrt
265813 glzz.mjv
108147 nbb.ztq
229253 nvqsqtjw
85743 pgqmwn
261375 qnm
$ cd gbpnjrt
$ ls
179812 mtp.lnf
234550 shmvmqv.qvq
$ cd ..
$ cd ..
$ cd dhhghph
$ ls
dir ghmpb
70514 zqv.ssn
$ cd ghmpb
$ ls
132693 shmvmqv.csb
$ cd ..
$ cd ..
$ cd pgqmwn
$ ls
dir hcbtbr
200292 pgqmwn
67530 sgm
15442 snqcgbs
$ cd hcbtbr
$ ls
249252 dqvcqs.zpb
$ cd ..
$ cd ..
$ cd qpbn
$ ls
dir pmbw
261142 qnm
dir rsfh
239788 sgm
dir wfwl
$ cd pmbw
$ ls
dir gwwdtzp
dir jszzc
32725 rnggjs.cbv
dir sldv
dir tptrlz
$ cd gwwdtzp
$ ls
dir dlvbdn
63066 dslb.mrp
dir shmvmqv
dir tzssf
$ cd dlvbdn
$ ls
190731 nqv.csc
$ cd ..
$ cd shmvmqv
$ ls
223667 nbb.ztq
$ cd ..
$ cd tzssf
$ ls
27296 rlgswwzq.dgj
dir tfdq
206881 wrdqg.pfj
$ cd tfdq
$ ls
26512 wsgh
$ cd ..
$ cd ..
$ cd ..
$ cd jszzc
$ ls
218701 qpz.hdm
$ cd ..
$ cd sldv
$ ls
dir hsbzqtml
3895 jgsqvf.tql
181815 nbb.ztq
294048 shmvmqv.fcm
dir snqcgbs
dir tmw
203484 zfpjq.pvn
dir zlpwdp
$ cd hsbzqtml
$ ls
dir shmvmqv
$ cd shmvmqv
$ ls
103536 nbb.ztq
32635 wmc
$ cd ..
$ cd ..
$ cd snqcgbs
$ ls
95112 dch
$ cd ..
$ cd tmw
$ ls
dir dplm
275451 hsr.zhn
9766 qnm
177383 ztbmqjqb.hwj
$ cd dplm
$ ls
277671 dqvcqs.zpb
$ cd ..
$ cd ..
$ cd zlpwdp
$ ls
315030 snqcgbs.mlz
$ cd ..
$ cd ..
$ cd tptrlz
$ ls
dir jfflz
154685 qbbpstff.fcs
dir sjjgljg
dir zsssqcsb
$ cd jfflz
$ ls
1330 fvldwzn.dbb
227153 nbb.ztq
$ cd ..
$ cd sjjgljg
$ ls
295745 frjnfsgt.fmq
105131 jtbltpv.vhm
174279 pgqmwn
104317 sgm
$ cd ..
$ cd zsssqcsb
$ ls
246361 bnzsv.dqf
dir ggcvqf
dir htqpwzw
249833 jgsqvf.tql
dir lbtjth
dir lcfwb
306768 mmd.gjw
121112 pgqmwn
dir sgm
dir shmvmqv
dir snqcgbs
55055 tmw.rqm
$ cd ggcvqf
$ ls
46573 fcwznd
$ cd ..
$ cd htqpwzw
$ ls
dir bmzvdzsv
193713 fpp.vwd
39898 mfjcnl.szn
65370 msljctr
10685 ppgzbc.trg
94843 shmvmqv.hqn
dir thfdg
$ cd bmzvdzsv
$ ls
74898 pgqmwn.wwh
$ cd ..
$ cd thfdg
$ ls
dir cpsgcll
$ cd cpsgcll
$ ls
244683 gdfw
17203 tmw
$ cd ..
$ cd ..
$ cd ..
$ cd lbtjth
$ ls
dir snqcgbs
$ cd snqcgbs
$ ls
122394 grhbhpw.fsl
55324 jgsqvf.tql
$ cd ..
$ cd ..
$ cd lcfwb
$ ls
138053 nvqsqtjw
$ cd ..
$ cd sgm
$ ls
314313 dqvcqs.zpb
146274 fftqvs.ltt
225372 jtq.rzq
85697 rctzgq.rsr
dir sgm
262221 tqpng
$ cd sgm
$ ls
dir cmvvd
277366 mhs.tpn
77975 nbb.ztq
160966 qrwbvbd.ghf
55949 snqcgbs.qzc
$ cd cmvvd
$ ls
28054 lfsvfsl
165671 tmw.zwh
$ cd ..
$ cd ..
$ cd ..
$ cd shmvmqv
$ ls
51884 bsbwrjnz.dmt
231236 jfmsg.mts
dir qpb
256063 zhmw
65512 zrnhr
$ cd qpb
$ ls
228289 dqvcqs.zpb
$ cd ..
$ cd ..
$ cd snqcgbs
$ ls
dir mnwgf
$ cd mnwgf
$ ls
178834 qgnwvlfd.cps
255140 sls.mnw
9546 tvhbm
281615 vntrb
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd rsfh
$ ls
310413 snqcgbs
$ cd ..
$ cd wfwl
$ ls
dir fmvg
129025 jgsqvf.tql
$ cd fmvg
$ ls
276100 nvqsqtjw
$ cd ..
$ cd ..
$ cd ..
$ cd rwmsqdw
$ ls
46554 qzdmgl
55785 shmvmqv
127982 tfffrg.sbz
$ cd ..
$ cd shmvmqv
$ ls
dir ntstz
dir vprlz
$ cd ntstz
$ ls
304801 nvqsqtjw
28969 wzfb.fjv
$ cd ..
$ cd vprlz
$ ls
dir cmmtnt
dir shmvmqv
$ cd cmmtnt
$ ls
270129 shmvmqv
$ cd ..
$ cd shmvmqv
$ ls
dir sgm
$ cd sgm
$ ls
287889 hghh.mwt
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd pgqmwn
$ ls
85368 qnm
$ cd ..
$ cd tmw
$ ls
dir fnpjwg
dir gdt
dir ggs
dir llh
69470 nvqsqtjw
dir sgm
$ cd fnpjwg
$ ls
161820 dndq.tfl
dir dwmcs
205155 tdccsw.qlq
165352 vvmnpj
$ cd dwmcs
$ ls
121466 dqvcqs.zpb
274171 qbbqmf.ltf
$ cd ..
$ cd ..
$ cd gdt
$ ls
dir cnvtrw
dir gdcvdsv
dir hcn
dir hsmhn
dir jnhqz
78446 pgqmwn.mwr
dir qwfl
dir snqcgbs
$ cd cnvtrw
$ ls
dir hfvzm
$ cd hfvzm
$ ls
305880 tmw.lvw
$ cd ..
$ cd ..
$ cd gdcvdsv
$ ls
69690 jhscd
$ cd ..
$ cd hcn
$ ls
166775 dqvcqs.zpb
294325 nvqsqtjw
$ cd ..
$ cd hsmhn
$ ls
172897 lrjf.dnw
dir npgr
225227 pbcmjg.rhf
234530 pgqmwn.cqb
dir qlz
dir snqcgbs
52808 tnntq.wjt
$ cd npgr
$ ls
141280 qnm
64422 rwv
$ cd ..
$ cd qlz
$ ls
258070 nbb.ztq
$ cd ..
$ cd snqcgbs
$ ls
3723 frcr.jmn
109486 sgm.nsw
114054 shmvmqv.frr
142024 snqcgbs
$ cd ..
$ cd ..
$ cd jnhqz
$ ls
dir gpztwnqm
dir nwpqbf
dir sgm
dir tmw
$ cd gpztwnqm
$ ls
303103 bjbpj
281422 hgp
284044 jgsqvf.tql
dir qzts
133248 sgm
$ cd qzts
$ ls
2428 dqvcqs.zpb
283206 hzs.smg
dir lmlw
$ cd lmlw
$ ls
dir sjrfmpc
$ cd sjrfmpc
$ ls
164708 nbb.ztq
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd nwpqbf
$ ls
192033 dqvcqs.zpb
298220 jqd.bfh
dir mhtsg
100921 snqcgbs
dir zbtb
$ cd mhtsg
$ ls
140813 cgcn.rjn
$ cd ..
$ cd zbtb
$ ls
201213 gjglrmf.dzv
dir grbl
81636 pgqmwn.zfm
265992 pwhbflzn.sch
dir twjrlzpj
$ cd grbl
$ ls
82894 jgsqvf.tql
$ cd ..
$ cd twjrlzpj
$ ls
304952 cwhtqv.zvh
$ cd ..
$ cd ..
$ cd ..
$ cd sgm
$ ls
56329 ftfvfpz.srv
3875 nbb.ztq
4766 shmvmqv.fdt
247272 vbw.hsv
$ cd ..
$ cd tmw
$ ls
dir ddbvd
$ cd ddbvd
$ ls
83200 tnrwdc.dzm
$ cd ..
$ cd ..
$ cd ..
$ cd qwfl
$ ls
dir bcfnz
23440 nvqsqtjw
dir pwb
206984 shmvmqv.fjb
35453 snqcgbs
297136 snqcgbs.fnr
dir tmw
181689 wcbdwwq
$ cd bcfnz
$ ls
dir fnrclwn
dir fslh
258656 gtbhdpt.pth
238261 psbsmvpf.lmt
dir sgm
$ cd fnrclwn
$ ls
30297 crhjfzlp.gtb
297557 gvvnbmt.spr
189301 nvqsqtjw
45159 sndmlvw.lsb
259295 sqd
$ cd ..
$ cd fslh
$ ls
246662 bpvcj.hnf
$ cd ..
$ cd sgm
$ ls
dir jpr
dir tmw
dir wscvtrd
$ cd jpr
$ ls
49005 dqvcqs.zpb
$ cd ..
$ cd tmw
$ ls
201387 sgm
$ cd ..
$ cd wscvtrd
$ ls
77175 blczlqf.pnv
207393 shmvmqv.qbs
$ cd ..
$ cd ..
$ cd ..
$ cd pwb
$ ls
89451 bqcmzzf
207548 gfwgnft.php
dir gsn
dir jtvpnvz
144792 nbb.ztq
dir nzzqj
214209 pgqmwn.sjv
236845 pzvjqqvz
dir sgm
dir snqcgbs
dir vfvttj
$ cd gsn
$ ls
93490 snqcgbs.fbv
$ cd ..
$ cd jtvpnvz
$ ls
dir fdnvff
208857 gpvwzhd
229559 rnc
225519 snqcgbs
162969 tmw
$ cd fdnvff
$ ls
286944 dztqqtf.nmp
dir rwc
181737 snqcgbs
$ cd rwc
$ ls
115290 nbb.ztq
$ cd ..
$ cd ..
$ cd ..
$ cd nzzqj
$ ls
154210 jgsqvf.tql
dir sgm
182438 snqcgbs.wpg
dir zpvghr
$ cd sgm
$ ls
123163 rjgc.ccm
dir rqn
291622 tflv.pqc
$ cd rqn
$ ls
220655 rtbrwmjn.clj
$ cd ..
$ cd ..
$ cd zpvghr
$ ls
288859 dqvcqs.zpb
218036 hfdcrjp
$ cd ..
$ cd ..
$ cd sgm
$ ls
107183 sgm.nsf
225832 sjwn
dir tgf
$ cd tgf
$ ls
258766 nvqsqtjw
$ cd ..
$ cd ..
$ cd snqcgbs
$ ls
56144 whjpg.ffz
$ cd ..
$ cd vfvttj
$ ls
dir ptmggmsl
$ cd ptmggmsl
$ ls
dir psd
dir wdzptvjc
$ cd psd
$ ls
121637 dqvcqs.zpb
$ cd ..
$ cd wdzptvjc
$ ls
dir shmvmqv
$ cd shmvmqv
$ ls
23754 dqmrj.fhh
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd tmw
$ ls
17390 hqz
dir jnsb
31072 nvqsqtjw
dir tmw
144511 vvwjcqr.rtz
$ cd jnsb
$ ls
265907 snqcgbs.swh
$ cd ..
$ cd tmw
$ ls
dir gfncpvw
$ cd gfncpvw
$ ls
dir ctvwgtlh
$ cd ctvwgtlh
$ ls
29825 ccjvs.qqq
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd ..
$ cd snqcgbs
$ ls
230355 ccl.ddg
dir csfdmhmb
dir drpgbvzf
313060 hhcpw.nbs
dir pfpn
dir ptwnpdnc
273250 qfsnmbm
dir shmvmqv
244767 tmw.zjg
$ cd csfdmhmb
$ ls
50690 nbb.ztq
282078 vzrw
$ cd ..
$ cd drpgbvzf
$ ls
dir bhwvqf
803 dqvcqs.zpb
49859 hhw.gpd
180202 nbb.ztq
dir nfh
64808 pgqmwn
276746 qnm
dir shmvmqv
dir tmw
$ cd bhwvqf
$ ls
76667 qcgpdwm.sbs
$ cd ..
$ cd nfh
$ ls
23774 jgsqvf.tql
34652 pnp.lvp
120264 zvp
$ cd ..
$ cd shmvmqv
$ ls
47748 cswm.dsr
dir pgqmwn
$ cd pgqmwn
$ ls
109587 qnm
$ cd ..
$ cd ..
$ cd tmw
$ ls
105578 snqcgbs
$ cd ..
$ cd ..
$ cd pfpn
$ ls
194628 wgc
$ cd ..
$ cd ptwnpdnc
$ ls
142456 rvwtfvr.tsc
$ cd ..
$ cd shmvmqv
$ ls
118642 hgqlrt.btl
$ cd ..
$ cd ..
$ cd ..
$ cd ggs
$ ls
231223 hmtp.djc
$ cd ..
$ cd llh
$ ls
dir hbq
dir svt
$ cd hbq
$ ls
157243 fhpsb
36981 rdbqnbfr.cwz
282257 tmw.wwv
$ cd ..
$ cd svt
$ ls
161395 sgm.hdl
$ cd ..
$ cd ..
$ cd sgm
$ ls
247276 fss
dir pgqmwn
68084 pgqmwn.vfl
28475 sgm.swb
dir shmvmqv
223438 snqcgbs.bng
$ cd pgqmwn
$ ls
134640 dqvcqs.zpb
dir nrdd
25431 nvqsqtjw
149751 qhzqw
dir vfmcjhwz
dir zlfhr
$ cd nrdd
$ ls
dir pgqmwn
$ cd pgqmwn
$ ls
57090 nfbd
$ cd ..
$ cd ..
$ cd vfmcjhwz
$ ls
24256 fdzfwfh.rcv
$ cd ..
$ cd zlfhr
$ ls
20504 fgbpndj.wgm
181769 qnm
$ cd ..
$ cd ..
$ cd shmvmqv
$ ls
208988 nbb.ztq
$ cd ..
$ cd ..
$ cd ..
$ cd vtm
$ ls
dir lqvjm
dir msmtcnsj
dir qmtgdn
dir tmw
$ cd lqvjm
$ ls
239772 hnj.zqf
$ cd ..
$ cd msmtcnsj
$ ls
48711 clfl.fsl
245929 ddh.gdq
dir fvlb
241040 nvqsqtjw
dir shmvmqv
305567 vdzzqpz.jfs
$ cd fvlb
$ ls
25669 ddz.tbc
$ cd ..
$ cd shmvmqv
$ ls
283905 nvqsqtjw
$ cd ..
$ cd ..
$ cd qmtgdn
$ ls
246479 pgqmwn
35386 rtf.cdp
217551 sgm.mrz
$ cd ..
$ cd tmw
$ ls
dir qfggmdvd
dir tlv
dir tmw
$ cd qfggmdvd
$ ls
288091 djwfq.wzc
37383 pwnzdj
214119 shmvmqv.wlg
29747 tmw.ntw
146556 zgbd
$ cd ..
$ cd tlv
$ ls
142924 grj.qhw
210672 qhmn
209554 zbcqcb.mhn
141215 zjmrgw.wlc
$ cd ..
$ cd tmw
$ ls
dir fwsgn
dir tmw
$ cd fwsgn
$ ls
dir qbbhrst
306142 sgm
$ cd qbbhrst
$ ls
dir sgm
dir srbn
$ cd sgm
$ ls
80992 bbvqrcf.chp
$ cd ..
$ cd srbn
$ ls
161382 nvqsqtjw
$ cd ..
$ cd ..
$ cd ..
$ cd tmw
$ ls
219508 dqvcqs.zpb
dir tmw
134434 wbvvgqwn.bmh
$ cd tmw
$ ls
209116 pgqmwn
""";

}