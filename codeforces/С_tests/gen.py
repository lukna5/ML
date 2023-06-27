import numpy as np
import random
import sys

max_shape = 5
max_depth = 3
max_layer_shape = 40

def wrandint(a, b, w):
    if w == 0:
        return random.randint(a, b)
    f = max if w > 0 else min
    return f(random.randint(a, b) for i in range(abs(w)))

def generate_test():
    layers = [("out", random.randint(2, max_shape), random.randint(1, max_depth))]
    op_count = random.randint(10, 20)
    for _ in range(op_count):
        op = random.randint(1, 5)
        if op == 1 or op == 2:
            fold_type = np.random.choice(["cnvm", "cnve", "cnvc"])
            in_depth = random.randint(1, max_depth)
            out_depth = layers[-1][2]
            out_shape = layers[-1][1]
            s = random.randint(1, 3)
            os = (out_shape - 1) * s
            k = random.randint(s, 3)
            p = random.randint(0, max(0, min(k - 1, (os + k - 3) // 2)))
            in_shape = os + k - 2 * p
            if in_shape > max_layer_shape: continue
            a = np.random.randint(-10, 10, in_depth * out_depth * k * k)
            layers.append((fold_type, in_shape, in_depth, [out_depth, k, s, p] + list(a)))
        elif op == 3:
            out_depth = layers[-1][2]
            out_shape = layers[-1][1]
            layers.append(("relu", out_shape, out_depth, [random.randint(1, 100)]))
        elif op == 4:
            s = random.randint(2, 3)
            out_depth = layers[-1][2]
            out_shape = layers[-1][1]
            if out_shape * s > max_layer_shape: continue
            layers.append(("pool", out_shape * s, out_depth, [s]))
        elif op == 5:
            out_depth = layers[-1][2]
            out_shape = layers[-1][1]
            b = np.random.randint(-10, 10, out_depth)
            layers.append(("bias", out_shape, out_depth, b))
    layers.reverse()
    inp_shape = layers[0][1]
    inp_dim = layers[0][2]
    input_values = np.random.randint(-10, 10, inp_shape ** 2 * inp_dim)
    print(inp_shape, inp_dim, *input_values)
    print(len(layers) - 1)
    for l in layers[:-1]:
        print(l[0], *l[3])
    dout = np.random.randint(-10, 10, layers[-1][1] ** 2 * layers[-1][2])
    print(*dout)
    #print(layers["N"], layers["M"], layers["K"])
    #for b in layers["blocks"]:
    #    print(*b)
    #for i in layers["inputs"]:
    #    for l in i.tolist():
    #        print(*l)
    #for i in layers["backwards"]:
     #   for l in i.tolist():
      #      print(*l)

def main():
    random.seed("|".join(sys.argv))
    np.random.seed(random.randint(0, 2**32 - 1))

    generate_test()



if __name__ == "__main__":
    main()