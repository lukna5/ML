import numpy as np
import random
import sys

max_shape_size = 20

def wrandint(a, b, w):
    if w == 0:
        return random.randint(a, b)
    f = max if w > 0 else min
    return f(random.randint(a, b) for i in range(abs(w)))

def generate_test():
    test_info = {
        "N": 0,                                      
        "M": 0,
        "K": 0,
        "blocks": [],
        "inputs": [],
        "backwards": [],
    }
    op = random.randint(1, 5)
    if op == 1 or op == 2:
        inp_cnt = random.randint(1, 5)
        op_inp = np.random.choice(np.arange(1, inp_cnt + 1), wrandint(1, inp_cnt, 3))
        test_info["N"] = 1 + inp_cnt
        test_info["M"] = inp_cnt
        test_info["K"] = 1
        shape_x = random.randint(1, max_shape_size)
        shape_y = random.randint(1, max_shape_size)
        test_info["blocks"] = [("var", shape_x, shape_y) for _ in range(inp_cnt)] + \
            [("sum" if op == 1 else "had", len(op_inp), *op_inp)]
        for _ in range(inp_cnt):
            test_info["inputs"].append(np.random.randint(-10, 10, (shape_x, shape_y)))
        test_info["backwards"].append(np.random.randint(-10, 10, (shape_x, shape_y)))
    elif op == 3:
        test_info["N"] = 2
        test_info["M"] = 1
        test_info["K"] = 1
        shape_x = random.randint(1, max_shape_size)
        shape_y = random.randint(1, max_shape_size)
        test_info["blocks"] = [("var", shape_x, shape_y), ("rlu", random.randint(1, 100), 1)]
        test_info["inputs"].append(np.random.randint(-10, 10, (shape_x, shape_y)))
        test_info["backwards"].append(np.random.randint(-10, 10, (shape_x, shape_y)))
    elif op == 4:
        test_info["N"] = 2
        test_info["M"] = 1
        test_info["K"] = 1
        shape_x = random.randint(1, max_shape_size)
        shape_y = random.randint(1, max_shape_size)
        test_info["blocks"] = [("var", shape_x, shape_y), ("tnh", 1)]
        test_info["inputs"].append(np.random.randint(-10, 10, (shape_x, shape_y)))
        test_info["backwards"].append(np.random.randint(-10, 10, (shape_x, shape_y)))
    elif op == 5:
        test_info["N"] = 3
        test_info["M"] = 2
        test_info["K"] = 1
        shape_x = random.randint(1, max_shape_size)
        shape_y = random.randint(1, max_shape_size)
        shape_z = random.randint(1, max_shape_size)
        if random.randint(1, 2) == 1:
            test_info["blocks"] = [("var", shape_x, shape_y), ("var", shape_y, shape_z), ("mul", 1, 2)]
            test_info["inputs"].append(np.random.randint(-10, 10, (shape_x, shape_y)))
            test_info["inputs"].append(np.random.randint(-10, 10, (shape_y, shape_z)))
        else:
            test_info["blocks"] = [("var", shape_y, shape_z), ("var", shape_x, shape_y), ("mul", 2, 1)]
            test_info["inputs"].append(np.random.randint(-10, 10, (shape_y, shape_z)))
            test_info["inputs"].append(np.random.randint(-10, 10, (shape_x, shape_y)))

        test_info["backwards"].append(np.random.randint(-10, 10, (shape_x, shape_z)))


    print(test_info["N"], test_info["M"], test_info["K"])
    for b in test_info["blocks"]:
        print(*b)
    for i in test_info["inputs"]:
        for l in i.tolist():
            print(*l)
    for i in test_info["backwards"]:
        for l in i.tolist():
            print(*l)

def main():
    random.seed("|".join(sys.argv))
    np.random.seed(random.randint(0, 2**32 - 1))

    generate_test()



if __name__ == "__main__":
    main()