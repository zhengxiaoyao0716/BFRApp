package com.zhengxiaoyao0716.bfrcli;

import com.zhengxiaoyao0716.bfrmanage.BFRManager;

import java.util.Scanner;

/**
 * 仿命令行控制.
 * Created by zhengxiaoyao0716 on 2015/12/5.
 */
public class Main {
    //private static BFRManager bfrManager = BFRManager.getManager("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "1", "xxxxx");
    private static BFRManager bfrManager = BFRManager.getManager("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "1", "xxxxx");

    private static boolean isRun = true;
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        if (args.length > 0)
        {
            StringBuilder cmdBuilder = new StringBuilder(args[0]);
            for (int index = 1; index < args.length; index++)
                cmdBuilder.append(" ").append(args[index]);
            parseCmd(cmdBuilder.toString(), scanner);
        }
        else while (isRun) {
            String cmd = scanner.nextLine();
            parseCmd(cmd, scanner);
        }
    }
    public static void parseCmd(String cmd, Scanner scanner)
    {
        if (cmd.contains("createApp")) {
            bfrManager.createApp();

        } else if (cmd.contains("listApp")) {
            bfrManager.listApp();

            //组操作
        } else if (cmd.contains("createGroup ")) {
            bfrManager.createGroup(cmd.split(" ")[1]);

        } else if (cmd.contains("deleteGroup ")) {
            bfrManager.deleteGroup(cmd.split(" ")[1]);

        } else if (cmd.contains("queryGroup ")) {
            bfrManager.queryGroup(cmd.split(" ")[1]);

        } else if (cmd.contains("listGroup")) {
            bfrManager.listGroup();

            //成员操作
        } else if (cmd.contains("createPerson ")) {
            System.out.print("facePaths=");
            String paths = scanner.nextLine();
            bfrManager.createPerson(cmd.split(" ")[1], cmd.split(" ")[2], FaceUtil.faces(paths.split(" ")));

        } else if (cmd.contains("deletePerson ")) {
            bfrManager.deletePerson(cmd.split(" ")[1]);

        } else if (cmd.contains("modifyPerson ")) {
            System.out.print("facePaths=");
            String paths = scanner.nextLine();
            bfrManager.modifyPerson(cmd.split(" ")[1], FaceUtil.faces(paths.split(" ")));

        } else if (cmd.contains("queryPerson ")) {
            bfrManager.queryPerson(cmd.split(" ")[1]);

        } else if (cmd.equals("listPerson")) {
            bfrManager.listPerson();

        } else if (cmd.contains("listPersonWhereGroup ")) {
            bfrManager.listPersonWhereGroup(cmd.split(" ")[1]);

            //验证操作
        } else if (cmd.contains("verify ")) {
            bfrManager.verify(cmd.split(" ")[1], "base64", FaceUtil.toBase64(cmd.split(" ")[2]));

        } else if (cmd.contains("identify ")) {
            bfrManager.identify(cmd.split(" ")[1], "base64", FaceUtil.toBase64(cmd.split(" ")[2]));

            //系统操作
        } else if (cmd.contains("exit")) {
            isRun = false;
        } else {
            System.out.println("Unknown command.");
        }
    }
}
