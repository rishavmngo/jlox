#!/bin/python3
import os


def run_command(command):
    """Runs a Maven command."""
    os.system(command)


def main():
    print("Select an option:")
    print("1. Run Lox with (sample.lox)")
    print("2. Run Lox with repl")
    print("3. Run GenerateAst tool")

    option = input("Enter your choice (1/2/3): ")

    if option == "1":
        command = "mvn exec:java -Dexec.mainClass=com.rishavmngo.lox.Lox -Dexec.args=sample.lox"
        run_command(command)
    elif option == "2":
        command = "mvn exec:java -Dexec.mainClass=com.rishavmngo.lox.Lox"
        run_command(command)
    elif option == "3":
        command = "mvn exec:java -Dexec.mainClass=com.rishavmngo.tool.GenerateAst -Dexec.args=./src/main/java/com/rishavmngo/lox"
        run_command(command)
    else:
        print("Invalid option. Please try again.")


if __name__ == "__main__":
    main()
