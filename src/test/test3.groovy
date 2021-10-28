import java.util.List;

def abc() {
println 'b'
}

def aaa() {
println 'f'
}

abc()

try {
 b = 0;
println 'c'
}catch(e){
println 'c'
}finally{
println 'c'
}

synchronized(a) {
println 'd'
println 'd'
}

String a = new String("b")
if (a.equals("a")) {
System.out.println(a.toString())
} else if (a.equals("b")) {
System.out.println("2")
} else if (a.equals("c")) {
System.out.println("3")
} else {
System.out.println("1")
}

List c = null;

System.out.println("b")

for(int i =0; i< 3;i++) {
	System.out.println("z")
}

int i = 0
switch(i) {
case 0: 
int ii = 2
System.out.println("0")
break
default:
System.out.println("1")
}

System.out.println("c")
